import meta.Board;
import meta.Player;
import meta.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-04-04
 * Time: 22:45
 */
public abstract class Game {
    public static final Random RANDOM = new Random();

    protected boolean cheatingMode = false;
    protected Board board;
    protected List<Player> playerList;
    protected Player currentPlayer;

    public Game(boolean cheatingMode) {
        this.cheatingMode = cheatingMode;
        this.board = new Board();
        playerList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Player player = new Player("" + i);
            player.setCurrentSquare(board.getStartSquare());
            playerList.add(player);
        }
        if (cheatingMode) {
            System.out.println("Playing in Cheating Mode");
            System.out.println();
        }
    }

    /**
     * start game
     */
    public abstract void start();

    /**
     * play move
     *
     * @return
     */
    public abstract int playerMove();

    public abstract void processOnSquare(Square square);

    /**
     * throwing a twelve-sided die
     *
     * @return 1-12
     */
    public abstract int die(int max);

    /**
     * random select the first one to play
     */
    public abstract void randomStartPlayer();

    /**
     * get winner
     *
     * @return winner or null
     */
    public Player getWinner() {
        List<Player> playerListTmp = new ArrayList<>();
        for (Player player : playerList) {
            if (player.getRemainAmount() >= 0) {
                playerListTmp.add(player);
            }
        }
        if (playerListTmp.size() == 1) {
            return playerListTmp.get(0);
        }
        return null;
    }

    /**
     * get next player
     *
     * @return player
     */
    public Player nextPlayer() {
        int index = playerList.indexOf(currentPlayer);
        index = (index + 1) % playerList.size();
        return playerList.get(index);
    }

    /**
     * display game
     */
    public void displayGame() {
        board.display(playerList);
    }

    protected void sleep(int milliSecond) {
        try {
            Thread.sleep(milliSecond);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
