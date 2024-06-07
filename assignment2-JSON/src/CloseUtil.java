import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-16
 * Time: 21:23
 */
public class CloseUtil {

    public static void close(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        try {
            dataInputStream.close();
        } catch (Exception e) {
        }
        try {
            dataOutputStream.close();
        } catch (Exception e) {
        }
        try {
            socket.close();
        } catch (Exception e) {

        }
    }
}
