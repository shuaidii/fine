package comp1110.ass1.board;

import comp1110.ass1.Position;
import comp1110.ass1.caterpillar.Colour;
import comp1110.ass1.challenge.Challenge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class AddingRequirementsTest {
    private static Board freshBoard(int challengeNumber) {
        Challenge challenge = Challenge.challengeFromNumber(challengeNumber);
        return Board.fromChallenge(challenge.getLayout());
    }

    private static void test(
            Colour expect,
            int challengeNumber,
            Board board,
            Position position) {
        Assertions.assertEquals(
                expect,
                board.spotRequiredHeadColour(position),
                "calling spotRequiredHeadColour on challenge number "
                        + challengeNumber
                        + " with position: "
                        + position
        );
    }
    @Test
    void basic() {
        int challengeNumber = 8;
        Board board = freshBoard(challengeNumber);
        test(Colour.YELLOW, challengeNumber, board, new Position(4, 4));
        test(Colour.TURQUOISE, challengeNumber, board, new Position(1, 0));
        test(Colour.GREEN, challengeNumber, board, new Position(4, 3));
        test(null, challengeNumber, board, new Position(2, 0));
        test(null, challengeNumber, board, new Position(3, 0));
        test(null, challengeNumber, board, new Position(4, 0));
        test(null, challengeNumber, board, new Position(0, 2));
        test(null, challengeNumber, board, new Position(1, 2));
        test(null, challengeNumber, board, new Position(2, 2));
        test(null, challengeNumber, board, new Position(4, 2));
    }

    @Test
    void wildHead() {
        int challengeNumber = 18;
        Board board = freshBoard(challengeNumber);
        test(Colour.YELLOW, challengeNumber, board, new Position(4, 3));
        test(Colour.WILDCARD, challengeNumber, board, new Position(0, 1));
        test(Colour.WILDCARD, challengeNumber, board, new Position(4, 4));
        test(null, challengeNumber, board, new Position(1, 0));
        test(null, challengeNumber, board, new Position(2, 0));
        test(null, challengeNumber, board, new Position(3, 0));
        test(null, challengeNumber, board, new Position(4, 0));
        test(null, challengeNumber, board, new Position(0, 2));
        test(null, challengeNumber, board, new Position(1, 2));
        test(null, challengeNumber, board, new Position(2, 2));
        test(null, challengeNumber, board, new Position(4, 2));
    }
}
