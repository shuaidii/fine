import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AggregationServer {
    private static final int DEFAULT_PORT = 4567;
    private static final String BACKUP_FILE = "backup.txt";

    private Lamport lamport;
    private List<Weather> weatherList;
    private Map<String, Long> contentServerCommunicateMap = new ConcurrentHashMap<>();
    private LamportPriorityExecute lamportPriorityExecute;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        new AggregationServer(port);
    }

    public AggregationServer(int port) {
        this.lamport = new Lamport();
        this.weatherList = new ArrayList<>();
        this.recoverDataFromBackupFile();
        lamportPriorityExecute = new LamportPriorityExecute();
        lamportPriorityExecute.start();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("ATOM: Aggregation Server started at " + serverSocket.getLocalSocketAddress());
            System.out.println("ATOM: Waiting for connecting ... ");

            newestWeatherCheckSchedule();



            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("ATOM: Receive connect from " + socket.getLocalSocketAddress());
                    AggregationServerSocket aggregationServerSocket = new AggregationServerSocket(this, socket);
                    aggregationServerSocket.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("ATOM: Error in Aggregation Server");
            e.printStackTrace();
        }
    }

    public long lastCommunicateTime(String contentServer) {
        return contentServerCommunicateMap.getOrDefault(contentServer, -1L);
    }

    /**
     * addWeather
     *
     * @param weather weather
     */
    public synchronized void addOrUpdateWeather(Weather weather, String contentServer) {
        weather.setContentServer(contentServer);
        contentServerCommunicateMap.put(contentServer, System.currentTimeMillis());

        Weather old = getWeather(weather.getId());
        if (old != null) {
            System.out.println("ATOM: Update weather, ID: " + weather.getId());
            weatherList.remove(old);
        } else {
            System.out.println("ATOM: Add new weather, ID: " + weather.getId());
        }
        weatherList.add(weather);
        FileUtil.saveFile(weatherList, BACKUP_FILE);
        System.out.println("ATOM: update " + BACKUP_FILE);

    }

    /**
     * removeWeathers
     *
     * @param weathers weathers
     */
    public synchronized void removeWeathers(List<Weather> weathers) {
        if (weathers == null || weathers.size() == 0) {
            return;
        }
        weatherList.removeAll(weathers);
        FileUtil.saveFile(weatherList, BACKUP_FILE);
        System.out.println("ATOM: update " + BACKUP_FILE);
    }

    /**
     * getWeather
     *
     * @param id id
     * @return Weather
     */
    public synchronized Weather getWeather(String id) {
        for (Weather weather : weatherList) {
            if (weather.getId().equals(id)) {
                return weather;
            }
        }
        return null;
    }

    /**
     * Remove any items in the JSON that have come from
     * content servers which it has not communicated with for 30 seconds
     */
    protected void newestWeatherCheckSchedule() {
        Thread thread = new Thread(() -> {
            while (true) {
                List<Weather> removeWeathers = new ArrayList<>();
                for (Weather weather : weatherList) {
                    long lastTime = contentServerCommunicateMap.get(weather.getContentServer());
                    if (lastTime < System.currentTimeMillis() - 30 * 1000L) {
                        removeWeathers.add(weather);
                        System.out.println("ATOM: Remove json data: stationId: " + weather.getId());
                    }
                }
                removeWeathers(removeWeathers);
            }
        });
        thread.start();
    }

    public void incLamport() {
        lamport.incr();
        System.out.println("ATOM: Increment lamport clock: " + lamport.getCount());
    }

    public void updateLamport(int count) {
        lamport.update(count);
        System.out.println("ATOM: Update lamport clock: " + lamport.getCount());
    }

    public int getLamport() {
        return lamport.getCount();
    }

    public void addLamportPriorityMessage(LamportPriorityMessage lamportPriorityMessage) {
        lamportPriorityExecute.add(lamportPriorityMessage);
    }

    /**
     * load data from backup file
     */
    private void recoverDataFromBackupFile() {
        try {
            weatherList = FileUtil.readFile(BACKUP_FILE);
            if (weatherList.size() > 0) {
                System.out.println("ATOM: Load backup data size: " + weatherList.size());
                for (Weather weather : weatherList) {
                    contentServerCommunicateMap.put(weather.getContentServer(), System.currentTimeMillis());
                }
            }
        } catch (Exception e) {
            weatherList = new ArrayList<>();
            System.out.println("ATOM: Load backup data error");
            e.printStackTrace();
        }
    }
}