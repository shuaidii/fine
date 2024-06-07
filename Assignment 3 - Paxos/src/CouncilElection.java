import vote.Acceptor;
import vote.Learner;
import vote.Vote;
import vote.Proposer;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Council Election
 */
public class CouncilElection {
    private static final int PORT = 9000;
    public static final Random RANDOM = new Random();

    public List<Acceptor> acceptorList = new ArrayList<>();
    public List<Proposer> lastPropList = new ArrayList<>();
    public List<Proposer> proposalList = new ArrayList<>();
    public int acceptCount;

    /**
     * Constructor
     *
     * @param members      members
     * @param prop         prop
     * @param chance       chance
     * @param responseTime responseTime
     */
    public CouncilElection(List<String> members, List<String> prop, List<Double> chance, List<Integer> responseTime) {
        if (members.size() != chance.size() || prop.size() != responseTime.size()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < members.size(); i++) {
            this.lastPropList.add(new Proposer());
            this.acceptorList.add(new Acceptor(members.get(i), chance.get(i)));
            this.proposalList.add(new Proposer(responseTime.get(i), prop.get(i)));
        }
        this.acceptCount = Math.floorDiv(members.size() - 1, 2) + 1;
    }

    /**
     * Vote next
     *
     * @param proposerList proposers
     * @param time         currentTime
     * @return Proposer
     */
    private Proposer next(List<Proposer> proposerList, int time) {
        if (proposerList.isEmpty()) {
            return proposalList.get(RANDOM.nextInt(proposalList.size()));
        }

        Collections.sort(proposerList);
        Proposer proposer = proposerList.get(proposerList.size() - 1);
        return proposer.getTime() < time && proposer.getName() != null ?
                new Proposer(time + 1, proposer.getName()) : proposalList.get(RANDOM.nextInt(proposalList.size()));
    }

    /**
     * Vote start
     *
     * @param proposer
     */
    public void vote(Proposer proposer) {
        try {
            int time = 0;
            boolean end = false;
            while (true) {
                if (end) break;
                time++;
                System.out.println("[Session] " + time);
                System.out.println("[Proposer] " + proposer);

                List<Proposer> tmp = new ArrayList<>();
                int accepts = getAccepts(proposer, tmp);
                if (tmp.size() < acceptCount) {
                    System.out.println("[Session " + time + "] failed! Not enough votes");
                } else if (accepts < acceptCount) {
                    System.out.println("[Session " + time + "]  failed! Votes are less than the majority " + accepts);
                } else {
                    end = true;
                }

                if (!end) {
                    proposer = next(tmp, time);
                }

            }
            System.out.println("\n[Election Completed After " + time + " Sessions]");
            System.out.println("[FINAL RESULT] " + proposer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getAccepts
     *
     * @param proposer     Proposer
     * @param proposerList List<Proposer>
     * @return accept count
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private int getAccepts(Proposer proposer, List<Proposer> proposerList) throws IOException, ClassNotFoundException {
        int accepts = 0;
        int index = 0;
        while (index < acceptorList.size()) {
            Acceptor acceptor = acceptorList.get(index);
            if (!acceptor.getName().equals(proposer.getName())) {
                if (acceptorVote(proposer, proposerList, index, acceptor)) {
                    accepts++;
                }
            }
            index++;
        }
        return accepts;
    }

    private boolean acceptorVote(Proposer proposer, List<Proposer> proposerList, int index, Acceptor acceptor)
            throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket("127.0.0.1", PORT)) {
            // vote
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Vote(proposer, acceptor, lastPropList.get(index), acceptor.getVoteRate()));
            outputStream.flush();

            Learner learner = (Learner) new ObjectInputStream(socket.getInputStream()).readObject();
            if (learner != null) {
                if (!learner.isReceive() && !learner.isAccept()) {
                    return false;
                }
                Proposer result = learner.getResult();
                Proposer last = lastPropList.get(index);
                last.setTime(result.getTime());
                last.setName(result.getName());
                if (learner.isReceive()) {
                    proposerList.add(learner.getVote());
                }
                if (learner.isAccept()) {
                    return true;
                }
            }
        }
        return false;
    }
}
