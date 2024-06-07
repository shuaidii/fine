package vote;

import java.io.Serializable;

/**
 * Vote Proposer
 */
public class Proposer implements Serializable, Comparable<Proposer> {
    public int time;
    public String name;

    public Proposer() {
        this.time = -1;
    }

    public Proposer(int time, String name) {
        this.time = time;
        this.name = name;
    }

    @Override
    public int compareTo(Proposer o) {
        return Double.compare(time, o.time);
    }

    @Override
    public String toString() {
        return String.format("%s is eligible to become council president, total Time: %d", this.name, this.time);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}