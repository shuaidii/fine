import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class EchoHandler extends Thread {
    Socket clientSocket;
    //file fields
    String fileP;
    long[] fileRange;
    //time fields
    ZoneId zoneId = ZoneId.of("GMT");
    ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    String formattedDate = zonedDateTime.format(formatter);
    //last modified field
    String lastModifiedString;

    EchoHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            System.out.println("Connection successful");
            System.out.println("Waiting for input.....");

            DataOutputStream writer = new DataOutputStream(
                    new BufferedOutputStream(clientSocket.getOutputStream()));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            //Handling client request
            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.length() <= 0) {
                    break;
                }

                requestProcessing(inputLine);
            }
            File file = new File(this.fileP);
            //last modified
            if (file.exists()) {
                long lastModified = file.lastModified();
                LocalDateTime lastModifiedDate = LocalDateTime.ofEpochSecond(lastModified / 1000, 0, ZoneOffset.UTC);


                ZoneId zoneId_modified = ZoneId.of("GMT");
                ZonedDateTime zonedDateTime_modified = lastModifiedDate.atZone(zoneId_modified);
                DateTimeFormatter modified_formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

                lastModifiedString = zonedDateTime_modified.format(modified_formatter);

                if (fileRange != null) {
                    // 206 partial content
                    writer.writeBytes(response("206 Partial Content"));
                    serveFile(writer, file, this.fileRange[0], this.fileRange[1]);
                } else {
                    //200 ok
                    writer.writeBytes(response("200 OK"));
                    FileInputStream fis = new FileInputStream(fileP);//Add try-catch later
                    byte[] buffer = new byte[1024];

                    int bytesRead;

                    while ((bytesRead = fis.read(buffer)) != -1) {
                        writer.write(buffer, 0, bytesRead);
                    }
                    fis.close();
                }
            } else {
                // 404 not found
                writer.writeBytes(response("404 Not Found"));
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
                    DataOutputStream writer = new DataOutputStream(
                            new BufferedOutputStream(clientSocket.getOutputStream()));

                    writer.writeBytes(response("500 Internal Server Error"));

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }


    //Process the request line. Distinguish GET line and Range: line.
    public void requestProcessing(String inputLine) {
        if (inputLine.startsWith("GET")) {
            this.fileP = "./content" + inputLine.split(" ")[1];
        } else if (inputLine.startsWith("Range:")) {
            String[] range = inputLine.split("=")[1].split("-");
            this.fileRange = new long[]{Long.parseLong(range[0]), Long.parseLong(range[1])
            };
        }
    }

    public String extension(String path) {
        String extension = "";

        for (int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '.') {
                extension = path.substring(i, path.length());
                break;
            }
        }
        return extension;
    }


    public String response(String status_code) {
        String CRLF = "\r\n";
        StringBuilder response1 = new StringBuilder("HTTP/1.1 " + status_code + CRLF);
        if (this.fileRange != null && !status_code.equals("404 Not Found")) {
            response1.append("Accept-Ranges: Bytes").append(CRLF);
            response1.append("Content-Length: ").append(this.fileRange[1] - this.fileRange[0] + 1).append(CRLF);
            response1.append("Content-Range: ").append(this.fileRange[0]).append('-').append(this.fileRange[1])
                    .append('/').append(new File(this.fileP).length()).append(CRLF);
        }
        if (!status_code.equals("404 Not Found")) {
            response1.append("Content-Type: ").append(get_media_Type(extension(this.fileP))).append(CRLF);
        }
        response1.append("Connection: ").append("keep-alive").append(CRLF);
        response1.append("Date: ").append(formattedDate).append(CRLF);
        if (!status_code.equals("404 Not Found")) {
            response1.append("Last-Modified: ").append(lastModifiedString).append(CRLF);
        }
        response1.append(CRLF);
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


}
















