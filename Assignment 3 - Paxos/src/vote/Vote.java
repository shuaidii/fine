package vote;

import java.io.Serializable;

/**
 * Vote
 */
public class Vote implements Serializable {
    private final Proposer proposer;
    private final Acceptor acceptor;
    private final Proposer result;
    private final double voteRate;

    public Vote(Proposer proposer, Acceptor acceptor, Proposer result, double voteRate) {
        this.proposer = proposer;
        this.acceptor = acceptor;
        this.result = result;
        this.voteRate = voteRate;
    }

    public double getVoteRate() {
        return voteRate;
    }

    public Proposer getResult() {
        return result;
    }

    public Proposer getProposer() {
        return proposer;
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }
}