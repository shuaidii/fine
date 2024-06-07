import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Auction {
    private List<AuctionItem> auctionItemList = new ArrayList<>();
    private Cipher key = null;

    public Server() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        super();
        this.key = KeyLoad.cipherEncrypt(Files.readString(Paths.get("keys/testKey.aes")));
        initAuctionItem();
    }

    private void initAuctionItem() {
        for (int i = 0; i < 100; i++) {
            AuctionItem auctionItem = new AuctionItem();
            auctionItem.itemID = i;
            auctionItem.name = "AuctionItem_" + i;
            auctionItem.description = "AuctionItem description";
            auctionItem.highestBid = i * i + i;
            auctionItemList.add(auctionItem);
        }
    }

    @Override
    public SealedObject getSpec(int itemID) throws RemoteException {
        for (AuctionItem item : auctionItemList) {
            if (item.itemID == itemID) {
                try {
                    return new SealedObject(item, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            Server s = new Server();
            String name = "Auction";
            Auction stub = (Auction) UnicastRemoteObject.exportObject(s, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

}
