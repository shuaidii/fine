import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ServerSocketManage extends SocketManage {
    private Server server;

    public ServerSocketManage(Socket socket, Server server) throws IOException {
        super(socket);
        this.server = server;
    }

    @Override
    public Message processMessage(Message message) {
        if (message.getMessageType().equals(MessageType.QUERY)) {
            List<Restaurant> list = server.getRestaurantList();
            Message reply = new Message(MessageType.DELIVERY, server.getCenter());
            reply.setValue(list);
            if (list.size() == 0 && server.isFinished()) {
                reply.setMessageType(MessageType.FINISH);
            }
            return reply;
        }
        return null;
    }

}
