package comp1110.ass1.board;

import comp1110.ass1.caterpillar.Caterpillar;
import comp1110.ass1.challenge.Challenge;
import comp1110.ass1.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
class UnoccupiedPositionsTest {
    private static Board freshBoard(int challengeNumber) {
        Challenge challenge = Challenge.challengeFromNumber(challengeNumber);
        return Board.fromChallenge(challenge.getLayout());
    }

    private static void test(
            boolean expect,
            int challengeNumber,
            Board board,
            Position[] positions) {
        Assertions.assertEquals(
                expect,
                board.arePositionsUnoccupied(positions),
                "calling arePositionsUnoccupied on challenge number "
                        + challengeNumber
                        + " with positions: "
                        + Arrays.toString(positions)
                );
    }

    @Test
    void offBoard() {
        int challengeNumber = 6;
        Board board = freshBoard(challengeNumber);
        Position[] pos = new Position[] { new Position(4, 4) }; // Gate with on-board
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(-1, -1) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(0, -1) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(-30, -30) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(30, 30) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(10, 1) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(5, 5) };
        test(false, challengeNumber, board, pos);
    }

    @Test
    void spotOrNot() {
        int challengeNumber = 6;
        Board board = freshBoard(challengeNumber);
        Position[] pos = new Position[] { new Position(0, 0) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(1, 4) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(0, 4) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(4, 0) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(4, 4) };
        test(true, challengeNumber, board, pos);

        challengeNumber = 17;
        board = freshBoard(challengeNumber);
        pos = new Position[] { new Position(0, 0) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(1, 4) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(0, 4) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(4, 0) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(4, 4) };
        test(true, challengeNumber, board, pos);

        challengeNumber = 36;
        board = freshBoard(challengeNumber);
        pos = new Position[] { new Position(0, 0) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(1, 4) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(0, 4) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(4, 0) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(4, 4) };
        test(true, challengeNumber, board, pos);

        challengeNumber = 51;
        board = freshBoard(challengeNumber);
        pos = new Position[] { new Position(0, 0) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(1, 4) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(0, 4) };
        test(true, challengeNumber, board, pos);
        pos = new Position[] { new Position(4, 1) };
        test(false, challengeNumber, board, pos);
        pos = new Position[] { new Position(4, 3) };
        test(true, challengeNumber, board, pos);
    }

    @Test
    void multiplePositions() {
        int challengeNumber = 6;
        Board board = freshBoard(challengeNumber);
        Position [] pos = new Position[] {
                new Position(0, 4), // true
                new Position(4, 0), // true
                new Position(4, 4)  // true
        };
        test(true, challengeNumber, board, pos);
        pos = new Position[] {
                new Position(0, 4), // true
                new Position(4, 0), // true
                new Position(4, 4), // true
                new Position(0, 0), // false
                new Position(1, 4)  // false
        };
        test(false, challengeNumber, board, pos);

        challengeNumber = 17;
        board = freshBoard(challengeNumber);
        pos = new Position[] {
                new Position(1, 4), // true
                new Position(4, 0), // true
                new Position(4, 4)  // true
        };
        test(true, challengeNumber, board, pos);
        pos = new Position[] {
                new Position(1, 4), // true
                new Position(4, 0), // true
                new Position(4, 4), // true
                new Position(0, 0), // false
                new Position(0, 4)  // false
        };
        test(false, challengeNumber, board, pos);
    }

    @Test
    void occupiedSpot() {
        // NOTE: Relies on working caterpillar placement (tryPlaceCaterpillar), so tests linked
        int challengeNumber = 6;
        Board board = freshBoard(challengeNumber);
        Position[] pos = new Position[]{
                new Position(4, 0),
                new Position(4, 1),
                new Position(4, 2),
                new Position(4, 3),
                new Position(4, 4),
        };
        test(true, challengeNumber, board, pos);
        // Rely on yellow caterpillar being length 5 and orientated UP initially
        Caterpillar cater = Caterpillar.newYellow();
        Assertions.assertTrue(board.tryPlaceCaterpillar(cater, new Position(4, 0)));
        test(false, challengeNumber, board, pos);
        pos = new Position[]{
                new Position(4, 1),
                new Position(4, 2),
                new Position(4, 3)
        };
        test(false, challengeNumber, board, pos);
        pos = new Position[]{
                new Position(4, 4)
        };
        test(false, challengeNumber, board, pos);
    }

}