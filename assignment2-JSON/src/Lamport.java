public class Lamport {
    private int count = 0;

    public Lamport() {
        this.count = 0;
    }

    public Lamport(int count) {
        this.count = count;
    }

    public synchronized void update(int newCount) {
        this.count = Math.max(count, newCount) + 1;
    }

    public synchronized void incr() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized void setCount(int count) {
        this.count = count;
    }
}
