package vote;

import java.io.Serializable;

/**
 * Vote Learner
 */
public class Learner implements Serializable {
    public Proposer vote = null;
    public Proposer result = null;
    public boolean isReceive, isAccept;

    public Learner() {
    }


    public Learner(Proposer vote, Proposer result, boolean isReceive, boolean isAccept) {
        this.vote = vote;
        this.result = result;
        this.isReceive = isReceive;
        this.isAccept = isAccept;
    }

    public Proposer getVote() {
        return vote;
    }

    public void setVote(Proposer vote) {
        this.vote = vote;
    }

    public Proposer getResult() {
        return result;
    }

    public void setResult(Proposer result) {
        this.result = result;
    }

    public boolean isReceive() {
        return isReceive;
    }

    public void setReceive(boolean receive) {
        this.isReceive = receive;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        this.isAccept = accept;
    }
}