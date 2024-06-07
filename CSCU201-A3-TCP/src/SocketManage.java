import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class SocketManage extends Thread {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public SocketManage(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = (Message) inputStream.readObject();
                Message returnMessage = processMessage(message);
                sendMessage(returnMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract Message processMessage(Message message);

    public void sendMessage(Message message) {
        try {
            if (message != null) {
                outputStream.writeObject(message);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
