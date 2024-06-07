package socs.network.node;


import socs.network.message.SOSPFPacket;

import java.io.IOException;
import java.net.Socket;

public class LinkServiceThread extends LinkThread {
    public LinkServiceThread(Socket socket, Router hostRouter) throws IOException {
        super(socket, hostRouter);
    }

    public void processInput() throws IOException, ClassNotFoundException, InterruptedException {
        RouterDescription rd2 = new RouterDescription(socket.getInetAddress().getHostAddress(), (short) socket.getLocalPort(), null);
        this.link = new Link(router.rd, rd2);

        portSlot();

        System.out.println();
        SOSPFPacket packet = (SOSPFPacket) inputStream.readObject();
        System.out.println("received HELLO from " + packet.srcIP + ";");
        System.out.println("Do you accept this request? (Y/N)");
        router.waitInput();
        String input = router.getInput();
        if (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("yes")) {
            feedback(this, 2);
            router.waitInput();
            return;
        } else {
            feedback(this, 3);
//            router.waitInput();
        }

        while (true) {
//            SOSPFPacket packet = (SOSPFPacket) inputStream.readObject();
            packet = (SOSPFPacket) inputStream.readObject();
            if (packet == null || packet.sospfType != 0) {
                continue;
            }
            System.out.println();

            if (packet.sospfType == 0) {
                if (clientSIP == null) {
                    clientSIP = packet.srcIP;
                    this.link.setSIP(packet.srcIP);
                    this.link.setWeight(packet.weight);
                }
                System.out.println("received HELLO from " + packet.srcIP + ";");

                if (this.link.router2.status != RouterStatus.INIT) {
                    this.link.router2.status = RouterStatus.INIT;
                    System.out.println("set " + packet.srcIP + " STATE to INIT;");
                    feedback(this);
                } else {
                    this.link.router2.status = RouterStatus.TWO_WAY;
                    System.out.println("set " + packet.srcIP + " STATE to TWO_WAY;");
                    this.router.lsd.addLink(this.link);
                    for (LinkThread linkClientThread : this.router.linkThreads) {
                        lsaUpdate(linkClientThread);
                    }
                    router.waitInput();
                }
            } else if (packet.sospfType == 1 && this.link.router2.status == RouterStatus.TWO_WAY && packet.lsaArray.size() > 0) {
                if (this.router.lsd.lsaNotify(packet.lsaArray)) {
                    for (LinkThread lst : this.router.linkThreads) {
                        if (lst.clientSIP.equals(this.clientSIP)) {
                            continue;
                        }
                        lsaUpdate(lst);
                    }
                }
            }
        }
    }
}
