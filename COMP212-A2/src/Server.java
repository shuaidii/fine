import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class Server extends Thread {
    public static final Scanner SC = new Scanner(System.in);
    private static final int PORT = 20000;


    private boolean isComplete = false;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server start, receive client connect ...");
            while (true) {
                ServerSocketService serverSocketService = new ServerSocketService(serverSocket.accept());
                System.out.println("Client connected.");
                serverSocketService.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    class ServerSocketService extends Thread {
        private Socket socket;

        private ObjectInputStream objectInputStream;
        private PrintStream printStream;
        private DistributedAlgorithm algorithm;


        public ServerSocketService(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                this.printStream = new PrintStream(socket.getOutputStream());
                this.objectInputStream = new ObjectInputStream(socket.getInputStream());

                int algoID = objectInputStream.readInt();
                System.out.println("algoID: " + algoID);
                if (algoID == 1) {
                    algorithm = new LCR();
                } else if (algoID == 2) {
                    algorithm = new HS();
                }
                algorithm.execute(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            isComplete = true;
        }

        public int numOfProcessors() throws IOException {
            sendMessage("numOfProcessors");
            return objectInputStream.readInt();
        }

        public int getRound() throws IOException {
            sendMessage("getRound");
            return objectInputStream.readInt();
        }

        public Map<String, Object> exeHSProcess(int index) throws IOException, ClassNotFoundException {
            sendMessage("exeHSProcess " + index);
            return (Map<String, Object>) objectInputStream.readObject();
        }

        public Map<String, Object> exeLCRProcess(int index) throws IOException, ClassNotFoundException {
            sendMessage("exeLCRProcess " + index);
            return (Map<String, Object>) objectInputStream.readObject();
        }

        public void sendHSMessage() throws IOException {
            sendMessage("sendHSMessage");
        }


        public void sendLCRMessage() throws IOException {
            sendMessage("sendLCRMessage");
        }

        public void incRound() throws IOException {
            sendMessage("incRound");
        }

        public void sendMessage(String message) throws IOException {
            printStream.println(message);
            printStream.flush();
        }


    }
}
