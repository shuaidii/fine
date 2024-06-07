import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client extends Thread {
    public static Scanner SC = new Scanner(System.in);
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int PORT = 20000;

    private int algoID;
    private List<Processor> processorList;
    private ObjectOutputStream objectOutputStream;
    private BufferedReader bufferedReader;

    public static void main(String args[]) {
        System.out.println("Please choose a simulator for Leader Election Algorithm:");
        System.out.println("1) LCR simulator\t 2) HS simulator");
        int algoID;
        do {
            algoID = algoIDParser();
        } while (algoID == 0);

        Client client = new Client(algoID);
        client.start();
    }

    static public int algoIDParser() {
        try {
            int number = SC.nextInt();
            if (number == 1 || number == 2)
                return number;
            else {
                System.out.println("Sorry, invalid input.");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Sorry, invalid input.");
            System.exit(1);
        }
        return 0;
    }


    public Client(int algoID) {
        this.algoID = algoID;
    }

    @Override
    public void run() {
        int numOfProcessors = inputProcessorNumber();
        this.processorList = Processor.ringConstructor(numOfProcessors);

        System.out.println("Client start, connecting server ...");
        try {
            Socket socket = new Socket(SERVER_HOST, PORT);
            try {
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                objectOutputStream.writeInt(algoID);
                objectOutputStream.flush();

                String read = null;
                while ((read = bufferedReader.readLine()) != null) {
//                    System.out.println("Receive command: " + read);
                    if (read.equalsIgnoreCase("numOfProcessors")) {
                        objectOutputStream.writeInt(processorList.size());
                        objectOutputStream.flush();
                    } else if (read.equalsIgnoreCase("getRound")) {
                        objectOutputStream.writeInt(Message.getRound());
                        objectOutputStream.flush();
                    } else if (read.equalsIgnoreCase("incRound")) {
                        Message.incRound();
                    } else if (read.startsWith("exeHSProcess ")) {
                        int index = Integer.parseInt(read.split(" ")[1]);
                        objectOutputStream.writeObject(exeHSProcess(index));
                        objectOutputStream.flush();
                    } else if (read.startsWith("exeLCRProcess ")) {
                        int index = Integer.parseInt(read.split(" ")[1]);
                        objectOutputStream.writeObject(exeLCRProcess(index));
                        objectOutputStream.flush();
                    } else if (read.equalsIgnoreCase("sendHSMessage")) {
                        sendHSMessage();
                    } else if (read.equalsIgnoreCase("sendLCRMessage")) {
                        sendLCRMessage();
                    } else if (read.startsWith("The leader ID")) {
                        System.out.println(read);
                        break;
                    } else {
                        System.out.println();
                    }
                }
            } catch (Exception e) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendLCRMessage() {
        System.out.println("----------------------------After LCRMessage sent in round " + Message.getRound());
        System.out.format("%-10s%-1s%-10s%-1s%-12s\n", "MyID", "|", "receivedID", "|", "Status");
        for (Processor processor : processorList) {
            processor.sendLCRMessage();
        }
    }

    private void sendHSMessage() {
        System.out.println("----------------------------After message sent in round " + Message.getRound());
        System.out.format("%-10s%-1s%-10s%-10s%-10s%-1s%-10s%-10s%-10s%-1s%-10s%-1s%-12s\n", "Processor", "|", "receivedCLK", "Direction", "HopCount", "|", "ReceivedCCW", "Direction", "HopCount", "|", "ProcPhase", "|", "MyStatus");
        for (int index = 0; index < processorList.size(); index++) {
            processorList.get(index).sendHSMessage();
        }
    }


    private Map<String, Object> exeHSProcess(int index) {
        Processor processor = processorList.get(index);
        processor.exeHSProcess();

        Map<String, Object> map = new HashMap<>();
        map.put("myStatus", processor.getMyStatus());
        map.put("numOfMessageSent", Message.getNumOfMsgSent());
        map.put("phase", processor.getPhase());
        map.put("myID", processor.getMyID());
        map.put("round", Message.getRound());
        return map;
    }

    private Map<String, Object> exeLCRProcess(int index) {
        Processor processor = processorList.get(index);
        processor.exeLCRProcess();

        Map<String, Object> map = new HashMap<>();
        map.put("myStatus", processor.getMyStatus());
        map.put("numOfMessageSent", Message.getNumOfMsgSent());
        map.put("phase", processor.getPhase());
        map.put("myID", processor.getMyID());
        map.put("round", Message.getRound());
        return map;
    }

    public int inputProcessorNumber() {
        while (true) {
            System.out.print("Please enter the number of processors: ");
            try {
                int number = SC.nextInt();
                if (number < 10000 && number > 0) {
                    return number;
                }
            } catch (Exception e) {
            }
            System.out.println("Sorry, invalid input.");
        }
    }
}
