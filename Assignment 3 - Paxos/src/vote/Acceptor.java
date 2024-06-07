package vote;

import java.io.Serializable;

/**
 * Vote Acceptor
 */
public class Acceptor implements Serializable {
    private final String name;
    private final double voteRate;

    public Acceptor(String name, double voteRate) {
        this.name = name;
        this.voteRate = voteRate;
    }

    @Override
    public String toString() {
        return "Acceptor{" +
                "name='" + name + '\'' +
                ", voteRate=" + voteRate +
                '}';
    }

    public String getName() {
        return name;
    }

    public double getVoteRate() {
        return voteRate;
    }
}
