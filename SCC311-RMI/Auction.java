import javax.crypto.SealedObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-10-20
 * Time: 20:44
 */
public interface Auction extends Remote {
    public SealedObject getSpec(int itemID) throws RemoteException;
}
