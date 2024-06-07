import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class ContentServer {
    private final String serverIp;
    private final int serverPort;
    private final Lamport lamport;
    private final List<Weather> weathersList;

    private Socket contentSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public static void main(String[] args) throws IOException {
        String address = args[0];
        String file = args[1];
        if (address.startsWith("http://")) {
            address = address.substring(7);
        }
        String ip = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);

        ContentServer contentServer = new ContentServer(ip, port, FileUtil.readFile(file));
        contentServer.start();
    }

    public ContentServer(String serverIp, int serverPort, List<Weather> weathersList) {
        this.lamport = new Lamport(new Random().nextInt(100));
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.weathersList = weathersList;
    }

    private void start() {
        System.out.println("===========================================");
        System.out.println("server address: " + serverIp);
        System.out.println("server port: " + serverPort);
        System.out.println("weather size: " + weathersList.size());
        System.out.println("===========================================");
        try {
            contentSocket = new Socket(serverIp, serverPort);
            dis = new DataInputStream(contentSocket.getInputStream());
            dos = new DataOutputStream(contentSocket.getOutputStream());
            for (int i = 0; i < weathersList.size(); i++) {
                Weather weather = weathersList.get(i);
                System.out.println();
                System.out.println("Send weather content: " + (i + 1) + "/" + weathersList.size());
                sendContent(weather);
            }
        } catch (Exception e) {
            System.out.println("Server Error, closed");
            e.printStackTrace();
        } finally {
            CloseUtil.close(contentSocket, dis, dos);
        }
    }

    private void sendContent(Weather weather) throws IOException {
        int tryCount = 0;
        while (tryCount++ < 3) {
            lamport.incr();
            String message = generatePutMessage(weather);
            System.out.println("Generate send message:");
            System.out.println(message);
            System.out.println();

            dos.writeUTF(message);
            String receiveMessage = dis.readUTF();
            String statusCode = receiveMessage.split("@")[0];
            int count = Integer.parseInt(receiveMessage.split("@")[1]);
            lamport.update(count);

            if (statusCode.equals("201") || statusCode.equals("200")) {
                System.out.println("PUT request Successes: " + statusCode);
                break;
            } else if (statusCode.equals("204") || statusCode.equals("400") || statusCode.equals("500")) {
                System.out.println("PUT request failed: " + statusCode + ", try again...");
            } else {
                System.out.println("PUT request failed, with error status code: " + statusCode + ", try again...");
            }
        }
    }

    private String generatePutMessage(Weather weather) {
        String jsonDate = JsonUtil.toJsonString(weather);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PUT /weather.json HTTP/1.1").append(System.lineSeparator());
        stringBuilder.append("User-Agent: ATOMClient/1/0").append(System.lineSeparator());
        stringBuilder.append("Content-Type: application/json").append(System.lineSeparator());
        stringBuilder.append("Content-Length: ").append(jsonDate.length()).append(System.lineSeparator());
        stringBuilder.append("Lamport: ").append(lamport.getCount()).append(System.lineSeparator());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(jsonDate);
        return stringBuilder.toString();
    }
}