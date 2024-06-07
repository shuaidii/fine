import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final Scanner SCANNER = new Scanner(System.in);
    private String hostname;
    private int port;

    public static void main(String[] args) {
        System.out.println("Welcome to JoesTable v2.0!");
        System.out.print("Enter the server hostname: ");
        String hostname = SCANNER.nextLine();
        System.out.print("Enter the server port: ");
        int port = Integer.parseInt(SCANNER.nextLine());
        new Client(hostname, port);
    }

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        init();
    }

    private  void init() {
        try {
            Socket socket = new Socket(hostname, port);
            new ClientSocketManage(socket).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
