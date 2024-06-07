import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-06
 * Time: 21:22
 */
public class Player implements Runnable {
    private static int MAX = 10000;

    private List<Card> pile;
    private Card[] centerCards;
    private int player;

    private List<Card> handCards = new ArrayList<>();

    /**
     * Construct
     *
     * @param pile        player pile with 29 cards
     * @param centerCards 2 tops cards
     * @param player      player id
     */
    public Player(List<Card> pile, Card[] centerCards, int player) {
        this.pile = pile;
        this.centerCards = centerCards;
        this.player = player;
    }

    /**
     * thread run
     * User peterson algorithm
     */
    @Override
    public void run() {
        while (!Speed.end) {
            while (handCards.size() < 3 && pile.size() > 0) {
                pickNewCard();
            }

            // Peterson algorithm start
            Speed.flag[player] = true;
            Speed.turn = otherPlayer();

            // 进入去
            int count = 0;
            while (Speed.flag[otherPlayer()] && Speed.turn == otherPlayer() && !Speed.end) {
                count++;
                if (count > MAX) {
                    Speed.end = true;
                    Event.GameEndsAsDraw();
                    return;
                }
            }

            // 临界区
            if (Speed.end) {
                // game already end
                return;
            }

            int index = Speed.random.nextInt(2);
            if (!tryPlaceCard(index)) {
                tryPlaceCard(1 - index);
            }
            if (handCards.size() == 0 && pile.size() == 0) {
                Speed.end = true;
                Event.PlayerWins(player + 1);
            }

            // 退出区
            Speed.flag[player] = false;


        }
    }

    /**
     * try to place one card on one of the center top cards
     *
     * @param centerIndex center card index
     * @return placed or not
     */
    private boolean tryPlaceCard(int centerIndex) {
        Card center = centerCards[centerIndex];
        Card matchCard = null;
        for (Card card : handCards) {
            if (card.matches(center)) {
                matchCard = card;
                break;
            }
        }
        if (matchCard != null) {
            handCards.remove(matchCard);
            centerCards[centerIndex] = matchCard;
            Event.PlayerPlacesCard(player + 1, centerIndex + 1, matchCard);
        }
        return matchCard != null;
    }

    /**
     * get the other player id
     *
     * @return player id
     */
    private int otherPlayer() {
        return player == 0 ? 1 : 0;
    }

    /**
     * get one card for pile into hand
     */
    private void pickNewCard() {
        if (handCards.size() >= 3 || pile.size() == 0) {
            return;
        }
        Card card = pile.remove(0);
        Event.PlayerPicksNewCard(player + 1, card);
        handCards.add(card);
    }
}
