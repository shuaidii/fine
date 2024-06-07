import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-16
 * Time: 22:23
 */
public class LamportPriorityExecute extends Thread {
    private PriorityBlockingQueue<LamportPriorityMessage> queue = new PriorityBlockingQueue<>();

    @Override
    public void run() {
        try {
            while (true) {
                LamportPriorityMessage lamportPriorityMessage = queue.take();
                String waiting = "";
                LamportPriorityMessage[] array = queue.toArray(new LamportPriorityMessage[0]);
                for (int i = 0; i < array.length; i++) {
                    waiting += array[i].getLamport();
                    if (i != array.length - 1) {
                        waiting += ",";
                    }
                }
                System.out.printf("Current processing message: [lamport=%s][waitingMessage=%s]%n",
                        lamportPriorityMessage.getLamport(), waiting);

                lamportPriorityMessage.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(LamportPriorityMessage lamportPriorityMessage) {
        queue.offer(lamportPriorityMessage);
    }
}
