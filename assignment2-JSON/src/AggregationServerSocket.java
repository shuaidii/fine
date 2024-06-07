import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-16
 * Time: 19:54
 */
public class AggregationServerSocket extends Thread {

    private AggregationServer aggregationServer;
    private Socket socket;

    private DataInputStream dis;
    private DataOutputStream dos;

    public AggregationServerSocket(AggregationServer aggregationServer, Socket socket) {
        this.aggregationServer = aggregationServer;
        this.socket = socket;
    }

    @Override
    public synchronized void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String receiveMessage = dis.readUTF();
                System.out.println();
                System.out.println("ATOM: Receive Message from " + socket.getRemoteSocketAddress());
                System.out.println(receiveMessage);
                System.out.println();

                String[] lines = receiveMessage.split("\n");
                String firstLine = lines[0];
                if (firstLine.startsWith("PUT ")) {
                    processPut(new ArrayList<>(Arrays.asList(lines)));
                } else if (firstLine.startsWith("GET ")) {
                    processGet(new ArrayList<>(Arrays.asList(lines)));
                } else {
                    System.out.println("ATOM: Request Error");
                    sendCode("400");
                }

                aggregationServer.incLamport();
            }
        } catch (SocketTimeoutException s) {
            System.out.println("ATOM: Socket timeout, closed!");
//            s.printStackTrace();
        } catch (IOException e) {
            System.out.println("ATOM: Socket closed!");
//            e.printStackTrace();
        } finally {
            CloseUtil.close(socket, dis, dos);
        }

    }

    /**
     * process get
     *
     * @param messageLines
     */
    private void processGet(ArrayList<String> messageLines) {
        String firstLine = messageLines.get(0);
        String stationID = firstLine.split(" ")[1].substring(1);

        int lamport = 0;
        for (String line : messageLines) {
            line = line.trim();
            if (line.startsWith("Lamport:")) {
                lamport = Integer.parseInt(line.split(":")[1].trim());
                break;
            }
        }

        int finalLamport = lamport;
        LamportPriorityMessage lamportPriorityMessage = new LamportPriorityMessage(finalLamport) {
            @Override
            void execute() {
                try {
                    aggregationServer.updateLamport(finalLamport);
                    Weather weather = aggregationServer.getWeather(stationID);
                    if (weather == null) {
                        StringBuilder stringBuilder = new StringBuilder("500@");
                        stringBuilder.append(aggregationServer.getLamport()).append(System.lineSeparator());
                        stringBuilder.append("EMPTY");
                        dos.writeUTF(stringBuilder.toString());
                        return;
                    }
                    StringBuilder stringBuilder = new StringBuilder("200@");
                    stringBuilder.append(aggregationServer.getLamport()).append(System.lineSeparator());
                    stringBuilder.append(JsonUtil.toString(weather, false));
                    dos.writeUTF(stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ATOM: Socket closed!");
                }

            }
        };

        aggregationServer.addLamportPriorityMessage(lamportPriorityMessage);
    }

    /**
     * @param messageLines
     */
    private void processPut(ArrayList<String> messageLines) {
        List<String> jsonLines = new ArrayList<>();
        int lamport = 0;
        boolean startJson = false;
        for (String line : messageLines) {
            line = line.trim();
            if (line.equals("{")) {
                startJson = true;
                continue;
            }
            if (startJson) {
                jsonLines.add(line);
            } else if (line.startsWith("Lamport:")) {
                lamport = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        int finalLamport = lamport;
        LamportPriorityMessage lamportPriorityMessage = new LamportPriorityMessage(finalLamport) {
            @Override
            void execute() {
                aggregationServer.updateLamport(finalLamport);
                try {
                    if (jsonLines.size() == 0) {
                        System.out.println("ATOM: No content json data");
                        sendCode("204");
                    }

                    if (!jsonLines.get(jsonLines.size() - 1).equals("}")) {
                        System.out.println("ATOM: Error content json data");
                        sendCode("500");
                        return;
                    }
                    jsonLines.remove(jsonLines.size() - 1);

                    Weather weather = JsonUtil.parse(jsonLines, true);
                    if (weather == null) {
                        System.out.println("ATOM: Error content json data");
                        sendCode("500");
                        return;
                    }
                    String contentServer = getRemoteIp();
                    long lastCommunicateTime = aggregationServer.lastCommunicateTime(contentServer);
                    aggregationServer.addOrUpdateWeather(weather, contentServer);

                    if (lastCommunicateTime == -1) {
                        sendCode("201");
                    } else {
                        sendCode("200");
                    }
                } catch (IOException e) {
                    System.out.println("ATOM: Socket closed!");
                }
            }
        };

        aggregationServer.addLamportPriorityMessage(lamportPriorityMessage);
    }

    private void sendCode(String code) throws IOException {
        dos.writeUTF(code + "@" + aggregationServer.getLamport());
    }

    private String getRemoteIp() {
        String address = socket.getRemoteSocketAddress().toString();
        String ip = address;
        if (address.contains(":")) {
            ip = address.split(":")[0];
        }
        if (ip.startsWith("/")) {
            ip = ip.substring(1);
        }
        return ip;
    }

}
