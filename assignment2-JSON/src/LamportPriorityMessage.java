/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-16
 * Time: 22:33
 */
public abstract class LamportPriorityMessage implements Comparable<LamportPriorityMessage> {
    private int lamport;

    public LamportPriorityMessage(int lamport) {
        this.lamport = lamport;
    }

    @Override
    public int compareTo(LamportPriorityMessage o) {
        return Integer.compare(lamport, o.lamport);
    }

    abstract void execute();

    public int getLamport() {
        return lamport;
    }
}
