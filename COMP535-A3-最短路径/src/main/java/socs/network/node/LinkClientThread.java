package socs.network.node;

import socs.network.message.SOSPFPacket;

import java.io.IOException;
import java.net.Socket;

public class LinkClientThread extends LinkThread {

    public LinkClientThread(Socket socket, Router hostRouter, Link link) throws IOException {
        super(socket, hostRouter, link);
    }

    public void processInput() throws IOException, ClassNotFoundException {
        portSlot();

        feedback(this);
        SOSPFPacket packet = (SOSPFPacket) inputStream.readObject();
//        System.out.println("received HELLO from " + packet.srcIP + ";");
        if (packet.sospfType == 2) {
            System.out.println();
            System.out.println("Your attach request has been rejected;");
            router.waitInput();
            return;
        }
//        router.waitInput();

        while (true) {
            packet = (SOSPFPacket) inputStream.readObject();
            if (packet.sospfType == 0) {
                System.out.println();
                System.out.println("received HELLO from " + packet.srcIP + ";");
                this.link.setSIP(packet.srcIP);
                this.link.router2.status = RouterStatus.TWO_WAY;
                this.router.lsd.addLink(this.link); // Update the LSA of this router

                System.out.println("set " + packet.srcIP + " STATE to TWO_WAY;");
                feedback(this);

                for (LinkThread linkClientThread : router.linkThreads) {
                    lsaUpdate(linkClientThread);
                }
                router.waitInput();

            } else if (packet.sospfType == 1 && packet.lsaArray.size() > 0) {
                if (this.router.lsd.lsaNotify(packet.lsaArray)) {
                    for (LinkThread linkClientThread : this.router.linkThreads) {
                        if (linkClientThread.clientSIP.equals(this.clientSIP)) {
                            continue;
                        }
                        lsaUpdate(linkClientThread);
                    }
                }
            }
        }
    }

}
