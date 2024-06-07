
public class GameState {
    private int[] piles;
    private int turnCount;
    private String winner = null;
    private int tokenCountA = 0;
    private int tokenCountB = 0;

    public GameState() {
        piles = new int[3];
        piles[0] = 20;
        piles[1] = 20;
        piles[2] = 20;
        turnCount = 0;
    }

    public boolean aWinner() {
        return "A".equalsIgnoreCase(winner);
    }

    public boolean bWinner() {
        return "B".equalsIgnoreCase(winner);

    }

    public boolean isValidMove(int pile, int count) {
        if (pile < 1 || pile > 3 || count <= 0) {
            return false;
        }
        int toke = piles[pile - 1];
        return toke >= count;
    }

    public void moveA(int pile, int count) {
        turnCount++;
        piles[pile - 1] -= count;
        tokenCountA += count;
        if (isClean()) {
            winner = "A";
        }
    }

    public void moveB(int pile, int count) {
        turnCount++;
        piles[pile - 1] -= count;
        tokenCountB += count;
        if (isClean()) {
            winner = "B";
        }
    }

    private boolean isClean() {
        for (int token : piles) {
            if (token != 0) {
                return false;
            }
        }
        return true;
    }

    public int turns() {
        return turnCount;
    }

    public String toString() {
        return String.format("Current State: Pile 1 - %d, Pile 2 - %d, Pile 3 - %d.", piles[0], piles[1], piles[2]);
    }


}
