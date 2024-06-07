package socs.network.node;

import socs.network.message.SOSPFPacket;
import socs.network.util.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;


public class Router {
    private static final Random RANDOM = new Random();

    protected LinkStateDatabase lsd;

    RouterDescription rd = new RouterDescription();

    //assuming that all routers are with 4 ports
    public Link[] ports = new Link[4];
    protected List<LinkThread> linkThreads = new ArrayList<>();

    private volatile boolean threadInput = false;
    private volatile String threadInputMsg = null;

    public Router(Configuration config) {
        rd.simulatedIPAddress = config.getString("socs.network.router.ip");
        rd.processPortNumber = (short) (RANDOM.nextInt(20000) + 10000);
        try {
            rd.processIPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Exception at getting process IP. ");
            e.printStackTrace();
        }

        try {
            ServerSocket serverSocket = new ServerSocket(rd.processPortNumber);
            new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        new LinkServiceThread(socket, this).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Could not create server socket on port " + rd.processPortNumber + ". Quitting.");
            System.exit(-1);
        }

        display();

        lsd = new LinkStateDatabase(rd);
    }

    /**
     * output the shortest path to the given destination ip
     * <p/>
     * format: source ip address  -> ip address -> ... -> destination ip
     *
     * @param destinationIP the ip adderss of the destination simulated router
     */
    private void processDetect(String destinationIP) {
        String path = lsd.getShortestPath(destinationIP);
        System.out.println(path);
    }

    /**
     * disconnect with the router identified by the given destination ip address
     * Notice: this command should trigger the synchronization of database
     *
     * @param portNumber the port number which the link attaches at
     */
    private void processDisconnect(short portNumber) {

    }

    /**
     * attach the link to the remote router, which is identified by the given simulated ip;
     * to establish the connection via socket, you need to indentify the process IP and process Port;
     * additionally, weight is the cost to transmitting data through the link
     * <p/>
     * NOTE: this command should not trigger link database synchronization
     */
    private void processAttach(String processIP, short processPort,
                               String simulatedIP, short weight) {

        if (simulatedIP.equals(rd.simulatedIPAddress) || processPort == rd.processPortNumber) {
            System.out.println("The router can not connect to its own port or simulated IP address");
            return;
        }

        RouterDescription rd2 = new RouterDescription(processIP, processPort, simulatedIP);
        Link link = new Link(rd, rd2, weight);
        try {
            int index = 0;
            for (index = 0; index < ports.length; index++) {
                if (ports[index] == null) {
                    break;
                }
            }
            if (index >= 4) {
                System.out.println("All ports are filled. Ignoring.");
                return;
            }

            Socket socket = new Socket(processIP, processPort);
            new LinkClientThread(socket, this, link).start();
        } catch (IOException ioe) {
            System.out.println("Exception encountered on accept. Ignoring.");
            ioe.printStackTrace();
        }
    }


    /**
     * process request from the remote router.
     * For example: when router2 tries to attach router1. Router1 can decide whether it will accept this request.
     * The intuition is that if router2 is an unknown/anomaly router, it is always safe to reject the attached request from router2.
     */
    private void requestHandler() {

    }

    /**
     * broadcast Hello to neighbors
     */
    private void processStart() throws IOException {
        for (Link link : ports) {
            if (link == null) {
                continue;
            }

            String r2SIP = link.router2.simulatedIPAddress;
            if (r2SIP != null && link.router2.status != RouterStatus.TWO_WAY) {

                SOSPFPacket sospfPacket = new SOSPFPacket();
                sospfPacket.srcIP = sospfPacket.routerID = rd.simulatedIPAddress;
                sospfPacket.dstIP = sospfPacket.neighborID = r2SIP;
                sospfPacket.sospfType = 0;
                sospfPacket.weight = link.weight;

                for (LinkThread linkThread : linkThreads) {
                    if (Objects.equals(linkThread.clientSIP, r2SIP)) {
                        linkThread.outputStream.writeObject(sospfPacket);
                    }
                }
            }
        }
    }

    /**
     * attach the link to the remote router, which is identified by the given simulated ip;
     * to establish the connection via socket, you need to indentify the process IP and process Port;
     * additionally, weight is the cost to transmitting data through the link
     * <p/>
     * This command does trigger the link database synchronization
     */
    private void processConnect(String processIP, short processPort,
                                String simulatedIP, short weight) {

    }

    /**
     * output the neighbors of the routers
     */
    private void processNeighbors() {
        for (Link link : ports) {
            if (link == null || (link.router2.status != RouterStatus.TWO_WAY && link.router2.simulatedIPAddress == null)) {
                continue;
            }
            System.out.println(link.router2.simulatedIPAddress);
        }
    }

    /**
     * disconnect with all neighbors and quit the program
     */
    private void processQuit() {

    }

    /**
     * update the weight of an attached link
     */
    private void updateWeight(String processIP, short processPort,
                              String simulatedIP, short weight) {

    }

    public void terminal() {
        try {
            InputStreamReader isReader = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isReader);
            while (true) {
                System.out.print(">> ");
                String command = br.readLine();
                if (threadInput) {
                    threadInputMsg = command;
                    continue;
                }
                if (command.startsWith("detect ")) {
                    String[] cmdLine = command.split(" ");
                    processDetect(cmdLine[1]);
                } else if (command.startsWith("disconnect ")) {
                    String[] cmdLine = command.split(" ");
                    processDisconnect(Short.parseShort(cmdLine[1]));
                } else if (command.startsWith("quit")) {
                    processQuit();
                } else if (command.startsWith("attach ")) {
                    String[] cmdLine = command.split(" ");
                    processAttach(cmdLine[1], Short.parseShort(cmdLine[2]),
                            cmdLine[3], Short.parseShort(cmdLine[4]));
                } else if (command.equals("start")) {
                    processStart();
                } else if (command.equals("connect ")) {
                    String[] cmdLine = command.split(" ");
                    processConnect(cmdLine[1], Short.parseShort(cmdLine[2]),
                            cmdLine[3], Short.parseShort(cmdLine[4]));
                } else if (command.equals("neighbors")) {
                    //output neighbors
                    processNeighbors();
                } else {
                    //invalid command
                    System.out.println("Invalid command: " + command);
                    break;
                }
//                display();

            }
            isReader.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitInput() {
        System.out.print(">> ");
    }

    private void display() {
        System.out.println("=============================");
        System.out.println("processIPAddress: " + rd.processIPAddress);
        System.out.println("processPortNumber: " + rd.processPortNumber);
        System.out.println("simulatedIPAddress: " + rd.simulatedIPAddress);
        System.out.println("=============================");
    }

    public synchronized String getInput() throws InterruptedException {
        threadInput = true;
        while (threadInputMsg == null) {
            Thread.sleep(10);
        }
        threadInput = false;
        String msg = threadInputMsg;
        threadInputMsg = null;
        return msg;
    }
}
