import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class TCPServerHandler extends Thread {
    private static final String DATA_FILE = "data.dat";
    private static final String LOAD_VOD_PATH = "load/";
    private static final Map<String, Map<String, String>> PATH_MAP = new HashMap<>();

    public static final Map<String, Integer> VOD_LOAD_PERCENTAGE_MAP = new LinkedHashMap<>();
    public static final Map<String, Double> VOD_LOAD_RATE_MAP = new LinkedHashMap<>();


    Socket clientSocket;
    //file fields
    String fileP;
    long[] fileRange;
    //time fields
    ZoneId zoneId = ZoneId.of("GMT");
    ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    String formattedDate = zonedDateTime.format(formatter);
    private int rate;
    private String uri;


    static {
        loadData();
    }

    TCPServerHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private static void loadData() {
        List<String> lines = FileUtil.readFile(DATA_FILE);
        for (String line : lines) {
            String[] infos = line.split(" ");
            Map<String, String> map = new HashMap<>();
            map.put("path", infos[0]);
            map.put("host", infos[1]);
            map.put("port", infos[2]);
            map.put("rate", infos[3]);
            PATH_MAP.put(infos[0], map);
        }
    }


    public void run() {
        try {
//            System.out.println("Connection successful");
//            System.out.println("Waiting for input.....");

            DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //Handling client request
            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.length() <= 0) {
                    break;
                }
                requestProcessing(inputLine, writer);
            }
            writer.close();
            in.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                // 500 internal server error
                try {
                    DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                    writer.writeBytes(response("500 Internal Server Error"));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }


    //Process the request line. Distinguish GET line and Range: line.
    public void requestProcessing(String inputLine, DataOutputStream writer) throws IOException {

        if (inputLine.startsWith("GET")) {
            System.out.println(inputLine);
            uri = inputLine.split(" ")[1];
            if (uri.startsWith("/peer/add?")) {
                addProcess(uri, writer);
            } else if (uri.startsWith("/peer/view/")) {
                viewProcess(uri, writer);
            } else if (uri.startsWith("/peer/config?")) {
                configProcess(uri, writer);
            } else if (uri.equals("/peer/status")) {
                statusProcess(uri, writer);
            } else {
                writer.writeBytes(response("404 Not Found"));
            }
        }

        if (inputLine.startsWith("Range:")) {
            String[] range = inputLine.split("=")[1].split("-");
            this.fileRange = new long[]{
                    Long.parseLong(range[0]),
                    Long.parseLong(range[1])
            };
        }
    }

    private void statusProcess(String uri, DataOutputStream writer) throws IOException {
        String message = "";
        List<String> pathList = new ArrayList<>(VOD_LOAD_PERCENTAGE_MAP.keySet());
        Collections.reverse(pathList); // 反转
        for (String path : VOD_LOAD_PERCENTAGE_MAP.keySet()) {
            message += String.format("%s: percentage=%d%%, rate=%.1f%n", path, VOD_LOAD_PERCENTAGE_MAP.get(path), VOD_LOAD_RATE_MAP.get(path));
        }
        writer.writeBytes(response("200 OK", message));
    }

    private void configProcess(String uri, DataOutputStream writer) throws IOException {
        Map<String, String> paramMap = getRequestParam(uri);
        String rateStr = paramMap.get("rate");
        try {
            int rateInt = Integer.parseInt(rateStr);
            if (rateInt >= 0) {
                rate = rateInt;
            }
            UdpServer.setRate(rate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.writeBytes(response("200 OK", "Success: rate=" + rate));
    }

    /**
     * peer/view
     *
     * @param uri
     * @param writer
     * @throws IOException
     */
    private void viewProcess(String uri, DataOutputStream writer) throws IOException {
        String path = uri.substring(11);
        if (PATH_MAP.containsKey(path)) {
            Map<String, String> paramMap = PATH_MAP.get(path);
            int rate = Integer.parseInt(paramMap.get("rate"));
            // 尝试下载文件
            long fileSize = loadVod(path, paramMap.get("host"), paramMap.get("port"));
            if (fileSize > 0) {
                writer.writeBytes(response("200 OK", get_media_Type(extension(path)), null));
                FileInputStream fis = new FileInputStream(getLoadFilePath(path));//Add try-catch later

                // 根据rate进行传输，rate单位是k
                int byteSize = rate <= 0 ? 1024 : (int) Math.ceil(1000D * rate / 8);
                byte[] buffer = new byte[byteSize];
                int bytesRead;
                int count = 0;
                try {
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        count++;
                        writer.write(buffer, 0, bytesRead);
                        writer.flush();
                        if (rate > 0) {
                            // rate 限制, 沉睡1s
                            Thread.sleep(1000);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("count: " + count);
                    e.printStackTrace();
                } finally {
                    this.fileRange = null;
                }
                fis.close();
            } else {
                // 文件下载异常
                writer.writeBytes(response("404 Not Found"));
            }
        } else {
            // 文件未入库
            writer.writeBytes(response("404 Not Found"));
        }
    }

    /**
     * 下载vod文件
     *
     * @param path
     * @param host
     * @param port
     * @return
     */
    private long loadVod(String path, String host, String port) {
        String file = getLoadFilePath(path);
        File vod = new File(file);
        if (vod.exists()) {
            // vod已存在
            return vod.length();
        }

        try {
            // 下载
            byte[] vodByte = UdpClient.load(host, Integer.parseInt(port), path);
            if (vodByte == null) {
                return -1;
            }
            // 写入本地文件
            FileUtil.writeFile(file, vodByte);
            return vodByte.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * peer/add
     *
     * @param uri
     * @param writer
     * @throws IOException
     */
    private void addProcess(String uri, DataOutputStream writer) throws IOException {
        Map<String, String> paramMap = getRequestParam(uri);
        String data = paramMap.get("path") + " " + paramMap.get("host") + " " + paramMap.get("port") + " " + paramMap.get("rate");
        PATH_MAP.put(paramMap.get("path"), paramMap);
        FileUtil.writeFile(DATA_FILE, data, true);

        writer.writeBytes(response("200 OK", "Success"));
    }

    public String extension(String path) {
        String extension = "";
        for (int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '.') {
                extension = path.substring(i);
                break;
            }
        }
        return extension;
    }

    public String response(String status_code) {
        return response(status_code, "text/html", null);
    }

    public String response(String status_code, String data) {
        return response(status_code, "text/html", data);
    }

    public String response(String status_code, String contentType, String data) {
        String CRLF = "\r\n";
        StringBuilder response1 = new StringBuilder("HTTP/1.1 " + status_code + CRLF);
        if (this.fileRange != null && !status_code.equals("404 Not Found")) {
            response1.append("Accept-Ranges: Bytes").append(CRLF);
            response1.append("Content-Length: ").append(this.fileRange[1] - this.fileRange[0] + 1).append(CRLF);
            response1.append("Content-Range: ").append(this.fileRange[0]).append('-').append(this.fileRange[1])
                    .append('/').append(this.fileRange[1] + 1).append(CRLF);
        }

        response1.append("Content-Type: ").append(contentType).append(CRLF);
        response1.append("Connection: ").append("keep-alive").append(CRLF);
        response1.append("Date: ").append(formattedDate).append(CRLF);
        response1.append(CRLF);


        if (data != null) {
            response1.append(data);
        }
//        System.out.println(response1.toString());
        return response1.toString();
    }

    public static String get_media_Type(String extension) {
        Map<String, String> media_Types = new HashMap<>();
        media_Types.put(".txt", "text/plain");
        media_Types.put(".css", "text/css");
        media_Types.put(".html", "text/html");
        media_Types.put(".htm", "text/html");
        media_Types.put(".gif", "image/gif");
        media_Types.put(".jpg", "image/jpeg");
        media_Types.put(".jpeg", "image/jpeg");
        media_Types.put(".png", "image/png");
        media_Types.put(".js", "application/javascript");
        media_Types.put(".webm", "video/webm");
        media_Types.put(".mp4", "video/webm");
        media_Types.put(".ogg", "video/webm");
        String My_Type = media_Types.get(extension);
        if (My_Type != null) {
            return My_Type;
        } else {
            return "application/octet-stream";
        }

    }

    private String getLoadFilePath(String path) {
        return LOAD_VOD_PATH + path;
    }

    // For 206 partial content transfer.
    public void serveFile(DataOutputStream writer, File file, long start, long end) {
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.skip(start);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                if (start + bytesRead > end) {
                    bytesRead = (int) (end - start + 1);
                }
                writer.write(buffer, 0, bytesRead);
                start += bytesRead;
                if (start > end) {
                    break;
                }
            }
            fis.close();
        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, String> getRequestParam(String uri) {
        String params = uri.split("\\?")[1];
        Map<String, String> map = new HashMap<>();
        for (String kv : params.split("&")) {
            String key = kv.split("=")[0];
            String value = kv.split("=")[1];
            map.put(key, value);
        }
        return map;
    }

}
















