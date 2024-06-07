import vote.Acceptor;
import vote.Learner;
import vote.Vote;
import vote.Proposer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Vote Processor
 */
public class VoteServer extends Thread {
    private static final Random RANDOM = new Random();
    private static final int PORT = 9000;

    private Socket socket;

    public VoteServer(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Start vote server...");
        while (true) {
            // waiting vote connect
            System.out.println("Vote server waiting connect...");
            Socket socket = serverSocket.accept();
            new VoteServer(socket).start();
        }
    }


    /**
     * run vote process
     */
    public void run() {
        Learner learner = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Vote vote = (Vote) ois.readObject();

            Proposer proposer = vote.getProposer();
            System.out.println("Time: " + proposer.getTime());
            if (!(vote.getVoteRate() < 0) && !(vote.getVoteRate() > 1)) {
                Acceptor acceptor = vote.getAcceptor();
                if (RANDOM.nextDouble() >= vote.getVoteRate()) {
                    Proposer result = vote.getResult();
                    System.out.println(acceptor.getName() + " proposal received: [finalProposalTime=" + result.getTime() + "]");
                    if (proposer.getTime() > result.getTime()) {
                        learner = new Learner(result, proposer, true, RANDOM.nextDouble() - 0.5 > 0);
                        System.out.println(acceptor.getName() + " proposal: " + (learner.isAccept ? "accepted" : "rejected"));
                    } else {
                        learner = new Learner();
                        System.out.println(acceptor.getName() + " proposal: rejected");
                    }
                } else {
                    System.out.println(acceptor.getName() + " proposal: lost!");
                }
            } else {
                System.out.println("Vote error: chain value should be 0-1, current is " + vote.getVoteRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(learner);
        }
    }

    /**
     * close socket
     */
    private void close(Object returnObject) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(returnObject);
            oos.flush();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}