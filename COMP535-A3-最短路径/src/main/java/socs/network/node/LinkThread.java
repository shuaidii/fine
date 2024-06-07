package socs.network.node;

import socs.network.message.SOSPFPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;


public abstract class LinkThread extends Thread {
    protected Socket socket;
    protected ObjectInputStream inputStream;
    protected ObjectOutputStream outputStream;

    protected Router router;
    protected Link link;
    protected String clientSIP;
    protected int portSlotIndex = -1;


    LinkThread(Socket socket, Router hostRouter) throws IOException {
        this.socket = socket;
        this.router = hostRouter;
        this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    LinkThread(Socket socket, Router hostRouter, Link link) throws IOException {
        this(socket, hostRouter);
        this.link = link;
    }

    public abstract void processInput() throws IOException, ClassNotFoundException, InterruptedException;

    public void run() {
        try {
            processInput();
        } catch (IOException ioe) {
            System.err.println("IO exception in client handler");
            ioe.printStackTrace();
        } catch (ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally { // Clean up
            router.linkThreads.remove(this);
            if (portSlotIndex >= 0) {
                router.ports[portSlotIndex] = null;
            }
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delay() {
        Random random = new Random();
        int delay = random.nextInt(1000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void feedback(LinkThread linkThread) throws IOException {
        feedback(linkThread, 0);
    }

    protected void feedback(LinkThread linkThread, int sospfType) throws IOException {
        SOSPFPacket packet = new SOSPFPacket();
        packet.srcIP = packet.routerID = router.rd.simulatedIPAddress;
        packet.dstIP = packet.neighborID = linkThread.clientSIP;
        packet.sospfType = (short) sospfType;
        linkThread.outputStream.writeObject(packet);
    }

    protected void lsaUpdate(LinkThread linkThread) throws IOException {
        delay();

        SOSPFPacket packet1 = new SOSPFPacket();
        packet1.srcIP = packet1.routerID = router.rd.simulatedIPAddress;
        packet1.dstIP = packet1.neighborID = linkThread.clientSIP;
        packet1.sospfType = 1;

        packet1.lsaArray = new Vector<>(router.lsd._store.keySet().size());
        for (String key : router.lsd._store.keySet()) {
            packet1.lsaArray.add(router.lsd._store.get(key));
        }
        linkThread.outputStream.writeObject(packet1);
    }

    protected void portSlot() {
        int index = 0;
        for (index = 0; index < router.ports.length; index++) {
            Link link = router.ports[index];
            if (link == null) {
                router.ports[index] = this.link;
                router.linkThreads.add(this);
                if (router.ports[index].router2.simulatedIPAddress != null) {
                    clientSIP = router.ports[index].router2.simulatedIPAddress;
                }
                portSlotIndex = index;
                break;
            }
        }

        if (index >= 4) {
            throw new RuntimeException("There are no ports left.");
        }
    }

}
