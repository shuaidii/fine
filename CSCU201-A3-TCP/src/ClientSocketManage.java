import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

public class ClientSocketManage extends SocketManage {
    private static final double SPEED = 5000; // 5000km/h

    private Location center;
    private long startTime;

    public ClientSocketManage(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public Message processMessage(Message message) {
        if (message.getMessageType().equals(MessageType.WAIT)) {
            System.out.println(message.getValue().toString());
        } else if (message.getMessageType().equals(MessageType.START)) {
            this.center = message.getCenter();
            System.out.println(message.getValue().toString());
            this.startTime = System.currentTimeMillis();
            return new Message(MessageType.QUERY);
        } else if (message.getMessageType().equals(MessageType.DELIVERY)) {
            startDelivery((List<Restaurant>) message.getValue());
            return new Message(MessageType.QUERY);
        } else if (message.getMessageType().equals(MessageType.FINISH)) {
            displayTime();
            System.out.println("All orders completed!");
        }
        return null;
    }

    private void startDelivery(List<Restaurant> restaurantList) {
        if (restaurantList.size() == 0) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // 一起送
        for (Restaurant restaurant : restaurantList) {
            displayStartDeliveryMessage(restaurant);
        }

        Location start = center;
        while (restaurantList.size() > 0) {
            double minDistance = Double.MAX_VALUE;
            Restaurant nearestRestaurant = null; // 最近的餐厅
            for (Restaurant restaurant : restaurantList) {
                double distance = Help.getDistance(restaurant.getLocation(), start);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestRestaurant = restaurant;
                }
            }

            double timeHour = minDistance / 1000 / SPEED;
            double mills = timeHour * 3600 * 1000;
            try {
                Thread.sleep((long) mills);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 相同的餐厅，一起送达
            for (Iterator<Restaurant> iterator = restaurantList.iterator(); iterator.hasNext(); ) {
                Restaurant restaurant = iterator.next();
                if (restaurant.getName().equals(nearestRestaurant.getName())) {
                    displayFinishDeliveryMessage(restaurant);
                    iterator.remove();
                }
            }
            // 继续送的
            for (Restaurant restaurant : restaurantList) {
                displayContinueDeliveryMessage(restaurant);
            }
            start = nearestRestaurant.getLocation();
        }


        displayTime();
        System.out.println("Finished all deliveries, returning back to HQ.");

        // 返回
        double distance = Help.getDistance(start, center);
        double timeHour = distance / 1000 / SPEED;
        double mills = timeHour * 3600 * 1000;
        try {
            Thread.sleep((long) mills);
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayTime();
        System.out.println("Returned to HQ.");

    }

    private void displayStartDeliveryMessage(Restaurant restaurant) {
        displayTime();
        System.out.printf("Starting delivery of %s to %s.%n", restaurant.getFood(), restaurant.getName());
    }

    private void displayFinishDeliveryMessage(Restaurant restaurant) {
        displayTime();
        System.out.printf("Finished delivery of %s to %s.%n", restaurant.getFood(), restaurant.getName());
    }

    private void displayContinueDeliveryMessage(Restaurant restaurant) {
        displayTime();
        System.out.printf("Continuing delivery to %s.%n", restaurant.getName());
    }

    private void displayTime() {
        int time = (int) (System.currentTimeMillis() - startTime);
        int hour = time / 3600000;
        time = time % 360000;
        int minute = time / 60000;
        time = time % 60000;
        int second = time / 1000;
        int mills = time % 1000;
        System.out.printf("[%s:%s:%s.%s] ", withLength(hour, 2), withLength(minute, 2),
                withLength(second, 2), withLength(mills, 3));
    }

    private String withLength(int value, int length) {
        String data = value + "";
        while (data.length() < length) {
            data = "0" + data;
        }
        return data;
    }

    static class ClientDelivery extends Thread {

    }
}
