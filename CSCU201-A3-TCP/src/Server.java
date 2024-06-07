import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Server {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static int driverCount;
    private static Location center;
    private static List<Restaurant> restaurantList = new ArrayList<>();
    private static List<Restaurant> waitingDeliveryList = new ArrayList<>();

    public static void main(String[] args) {
        loadFile();
        System.out.println("\nWhat is your latitude?");
        double latitude = Double.parseDouble(SCANNER.nextLine());
        System.out.println("\nWhat is your longitude?");
        double longitude = Double.parseDouble(SCANNER.nextLine());
        center = new Location(latitude, longitude);
        for (Restaurant restaurant : restaurantList) {
            Location location = Help.getLocation(restaurant.getName(), center);
            restaurant.setLocation(location);
        }
        System.out.println("\nHow many drivers will be in service today?");
        driverCount = Integer.parseInt(SCANNER.nextLine());

        new Server();
    }

    public Server() {
        int connectCount = 0;
        try (ServerSocket serverSocket = new ServerSocket(3456)) {
            System.out.println("\nListening on port 3456. Waiting for drivers…");
            List<ServerSocketManage> serverSocketManageList = new ArrayList<>();
            while (connectCount < driverCount) {
                Socket socket = serverSocket.accept();
                System.out.println("\nConnection from " + serverSocket.getInetAddress().getHostAddress());
                connectCount++;
                ServerSocketManage serverSocketManage = new ServerSocketManage(socket, this);
                serverSocketManage.start();
                serverSocketManageList.add(serverSocketManage);

                Message message = null;
                if (connectCount == driverCount) {
                    System.out.println("\nStarting service.");
                    message = new Message(MessageType.START, center);
                    message.setValue("\nAll drivers have arrived! Starting service.");
                } else {
                    int need = driverCount - connectCount;
                    System.out.println("\nWaiting for " + need + " more driver(s)...");
                    message = new Message(MessageType.WAIT, center);
                    if (need == 1) {
                        message.setValue("\n1 more driver is needed before the service can begin. Waiting...");
                    } else {
                        message.setValue("\n" + need + " more drivers are needed before the service can begin. Waiting...");
                    }
                }
                for (ServerSocketManage serverSocketManageTmp : serverSocketManageList) {
                    serverSocketManageTmp.sendMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startDelivery();
    }

    private void startDelivery() {
        Runnable runnable = () -> {
            try {
                int second = 0;
                while (restaurantList.size() > 0) {
                    synchronized (waitingDeliveryList) {
                        for (Iterator<Restaurant> iterator = restaurantList.iterator(); iterator.hasNext(); ) {
                            Restaurant restaurant = iterator.next();
                            if (restaurant.getTime() == second) {
                                waitingDeliveryList.add(restaurant);
                                iterator.remove();
                            }
                        }
                    }
                    Thread.sleep(1000);
                    second++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }

    public boolean isFinished() {
        return restaurantList.isEmpty() && waitingDeliveryList.isEmpty();
    }

    private static void loadFile() {
        System.out.println("What is the name of the schedule file?");
        while (true) {
            String filename = SCANNER.nextLine();
            File file = new File(filename);
            if (!file.exists() || !file.isFile()) {
                System.out.println("\nThat file does not exist. What is the name of the schedule file?");
                continue;
            }
            try {
                List<String> lines = Files.readAllLines(Path.of(filename));
                for (String line : lines) {
                    String[] info = line.split(",");
                    Restaurant restaurant = new Restaurant(Integer.parseInt(info[0].trim()), info[1].trim(), info[2].trim());
                    restaurantList.add(restaurant);
                }
                return;
            } catch (Exception e) {
                restaurantList.clear();
                System.out.println("\nThat file is not properly formatted. What is the name of the schedule file?");
            }
        }
    }

    public List<Restaurant> getRestaurantList() {
        List<Restaurant> list = new ArrayList<>();

        synchronized (waitingDeliveryList) {
            if (waitingDeliveryList.size() == 0) {
                return list;
            }
            list.addAll(waitingDeliveryList);
            waitingDeliveryList.clear();
//            if (waitingDeliveryList.size() == 0) {
//                return new ArrayList<>();
//            }
//            int minSecond = Integer.MAX_VALUE;
//            for (Restaurant restaurant : waitingDeliveryList) {
//                if (restaurant.getTime() < minSecond) {
//                    minSecond = restaurant.getTime();
//                }
//            }
//            for (Restaurant restaurant : waitingDeliveryList) {
//                if (restaurant.getTime() == minSecond) {
//                    list.add(restaurant);
//                }
//            }
//            waitingDeliveryList.removeAll(list);
        }
        if (isFinished()) {
            System.out.println("\nAll orders completed!");
        }
        return list;
    }

    public Location getCenter() {
        return center;
    }
}
