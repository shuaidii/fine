import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class GETClient {
    private final String serverIp;
    private final int serverPort;
    private final Lamport lamport;
    private final String stationId;

    private Socket contentSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public static void main(String[] args) {
        String address = args[0];
        String stationId = args[1];
        if (address.startsWith("http://")) {
            address = address.substring(7);
        }
        String ip = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);

        GETClient contentServer = new GETClient(ip, port, stationId);
        contentServer.start();
    }

    public GETClient(String serverIp, int serverPort, String stationId) {
        this.lamport = new Lamport(new Random().nextInt(200));
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.stationId = stationId;
    }

    private void start() {
        System.out.println("===========================================");
        System.out.println("server address: " + serverIp);
        System.out.println("server port: " + serverPort);
        System.out.println("station id: " + stationId);
        System.out.println("===========================================");
        try {
            contentSocket = new Socket(serverIp, serverPort);
            dis = new DataInputStream(contentSocket.getInputStream());
            dos = new DataOutputStream(contentSocket.getOutputStream());
            sendMessage();
        } catch (Exception e) {
            System.out.println("Server Error, closed");
            e.printStackTrace();
        } finally {
            CloseUtil.close(contentSocket, dis, dos);
        }
    }

    private void sendMessage() throws IOException {
        lamport.incr();
        String message = generateGetMessage();
        System.out.println("Send message:");
        System.out.println(message);
        System.out.println();

        dos.writeUTF(message);

        String receiveMessage = dis.readUTF();
        String[] lines = receiveMessage.split("\n");
        String statusCode = lines[0].split("@")[0];
        int count = Integer.parseInt(lines[0].split("@")[1].trim());
        lamport.update(count);

        if (statusCode.equals("200")) {
            System.out.println("GET request Successes: " + statusCode);
            for (int i = 1; i < lines.length; i++) {
                System.out.println(lines[i]);
            }
        } else {
            System.out.println("GET request failed: " + statusCode);
            for (int i = 1; i < lines.length; i++) {
                System.out.println(lines[i]);
            }
        }
    }

    private String generateGetMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GET /").append(stationId).append(" HTTP/1.1").append(System.lineSeparator());
        stringBuilder.append("User-Agent: ATOMClient/1/0").append(System.lineSeparator());
        stringBuilder.append("Content-Type: application/txt").append(System.lineSeparator());
        stringBuilder.append("Lamport: ").append(lamport.getCount()).append(System.lineSeparator());
        return stringBuilder.toString();
    }

}

