import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Key;
import java.util.Arrays;
import java.util.Scanner;


public class Client {

    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Input auction item id: ");
//        int id = scanner.nextInt();
        if (args.length != 1) {
            System.out.println("Usage: java Client 5");
            return;
        }
        int id = Integer.parseInt(args[0]);
        System.out.println("Client start ...");
        try {
            Cipher key = KeyLoad.cipherDecrypt(Files.readString(Paths.get("keys/testKey.aes")));
            String name = "Auction";
            Registry registry = LocateRegistry.getRegistry("localhost");
            Auction server = (Auction) registry.lookup(name);
            SealedObject sealedObject = server.getSpec(id);
            if (sealedObject == null) {
                System.out.println("No found");
                return;
            }

            AuctionItem auctionItem = (AuctionItem) sealedObject.getObject(key);
            System.out.println("result is: " + auctionItem.name + " with " + auctionItem.highestBid);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
