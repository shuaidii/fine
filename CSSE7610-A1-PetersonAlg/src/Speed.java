import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 */
public class Speed {
    public static Random random = new Random(0);
    private static Card[] centerCards = new Card[2];

    public static int turn;
    public static boolean flag[] = new boolean[2];
    public static boolean end = false;

    public static void main(String[] args) {
        centerCards[0] = new Card(random);
        centerCards[1] = new Card(random);
        turn = random.nextInt(2);

        Thread one = initPlayer(0);
        Thread two = initPlayer(1);
        one.start();
        two.start();
    }

    /**
     * init player Thread
     *
     * @param player
     * @return Thread
     */
    private static Thread initPlayer(int player) {
        List<Card> pile = new ArrayList<>();
        for (int i = 0; i < 29; i++) {
            pile.add(new Card(random));
        }
        return new Thread(new Player(pile, centerCards, player));
    }
}
