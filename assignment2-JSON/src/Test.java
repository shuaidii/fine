import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-20
 * Time: 22:01
 */
public class Test {

    private static int port = 4567;
    private static List<Thread> threadList = new ArrayList<>();

    public static void main(String[] args) {
        startAggregationServer(4567);

        threadList.add(initContentServer("TEST 1: Content Server", "127.0.0.1:4567", "weather.txt"));
        threadList.add(initGetClient("TEST 2: Get Client Server", "127.0.0.1:4567", "IDS60901"));
        threadList.add(initGetClient("TEST 3: Get Client Server", "127.0.0.1:4567", "IDS111111"));
        threadList.add(initContentServer("TEST 4: Content Server", "127.0.0.1:4567", "weather2.txt"));
        threadList.add(initGetClient("TEST 5: Get Client Server", "127.0.0.1:4567", "IDS111111"));
        threadList.add(initGetClient("TEST 6: Get Client Server", "127.0.0.1:4567", "IDS000000"));

        for (Thread thread : threadList) {
            thread.start();
        }
    }

    private static Thread initGetClient(String message, String domainPort, String stationId) {
        return new Thread(() -> {
            try {
                System.out.println(message + " " + domainPort + " " + stationId);
                GETClient.main(new String[]{domainPort, stationId});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static Thread initContentServer(String message, String domainPort, String file) {
        return new Thread(() -> {
            try {
                System.out.println(message + " " + domainPort + " " + file);
                ContentServer.main(new String[]{domainPort, file});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void startAggregationServer(int port) {
        new Thread(() -> new AggregationServer(port)).start();
    }
}
