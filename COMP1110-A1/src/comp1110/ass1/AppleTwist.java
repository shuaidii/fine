package comp1110.ass1;

import comp1110.ass1.board.Board;
import comp1110.ass1.caterpillar.Caterpillar;
import comp1110.ass1.caterpillar.Colour;
import comp1110.ass1.challenge.Challenge;

/**
 * Apple Twist Puzzle
 *
 * This is constructed with a specific puzzle challenge, and encodes the puzzle state and logic.
 */
public class AppleTwist {
    // The puzzle board (apple)
    private final Board board;

    /* The caterpillars to be used in the game. There is always one turquoise,
       one yellow and one green caterpillar.
     */
    private final Caterpillar[] caterpillars = new Caterpillar[] {
            Caterpillar.newGreen(),
            Caterpillar.newTurquoise(),
            Caterpillar.newYellow()
    };

    // The specific puzzle challenge to solve
    public final Challenge challenge;

    /**
     * Generate a new random instance of Apple Twist with the provided difficulty.
     *
     * @param difficulty the given challenge difficulty
     */
    public AppleTwist(int difficulty) {
        this.challenge = Challenge.randomChallenge(difficulty);
        this.board = Board.fromChallenge(challenge.getLayout());
    }

    /**
     * Apple Twist with a specific challenge
     *
     * @param challenge specific challenge
     */
    public AppleTwist(Challenge challenge) {
        this.challenge = challenge;
        this.board = Board.fromChallenge(challenge.getLayout());
    }

    /**
     * @return the board state
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * @return the caterpillars
     */
    public Caterpillar[] getCaterpillars() {
        return this.caterpillars;
    }

    /**
     * Check if puzzle has been solved
     *
     * The caterpillars must be placed on the board, and any head colour
     * requirements for spots must be met.
     * @return
     */
    public boolean isPuzzleSolved() {
        for (Caterpillar caterpillar : caterpillars) {
            if (!caterpillar.isPlaced()) {
                return false;
            }
        }
        return true; // FIXME Task 9
    }
}
