import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class VodServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        UdpServer udpServer = null;

        if (args.length != 2) {
            System.out.println("Usage: java VodServer <TCP_PORT> <UDP_PORT>");
            System.exit(1);
            return;
        }

        int tcpPort = Integer.parseInt(args[0]);
        int udpPort = Integer.parseInt(args[1]);


        // UDP
        try {
            udpServer = new UdpServer(udpPort);
            udpServer.start();
        } catch (IOException e) {
            System.err.println("UDP could not listen on port: " + udpPort + ".");
            System.exit(1);
        }

        // TCP
        try {
            serverSocket = new ServerSocket(tcpPort);
        } catch (IOException e) {
            System.err.println("TCP could not listen on port: " + tcpPort + ".");
            System.exit(1);
        }

        try {
            while (true) {
//                System.out.println("Waiting for tcp connection.....");
                Socket clientSocket = serverSocket.accept();
                TCPServerHandler handler = new TCPServerHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        udpServer.close();
        serverSocket.close();
    }
} 