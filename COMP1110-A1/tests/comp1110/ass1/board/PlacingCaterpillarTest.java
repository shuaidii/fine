package comp1110.ass1.board;

import comp1110.ass1.challenge.Challenge;
import comp1110.ass1.Position;
import comp1110.ass1.caterpillar.Caterpillar;
import comp1110.ass1.caterpillar.Direction;
import comp1110.ass1.caterpillar.Segment;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class PlacingCaterpillarTest {

    // For the documentation in this class:
    // ###### KEY / LEGEND #######
    // "g | y | t" is a green or yellow or turquoise segment of caterpillar
    // "[g | y | t]" => head of a green or yellow or turquoise caterpillar
    // ###########################



    /**
     * Tests the case where a caterpillar is off the board
     * and then the tryPlaceCaterpillar() method is called.
     *
     * ########### Caterpillar correct placement #############
     *    0  1  2  3  4
     * 0    [t]
     * 1  t  t
     * 2  t
     * 3  t
     * 4  t
     *
     * ########################################################
     */

    @Test
    void offBoard() {
        // setting up challenge and board
        String challenge7 =  Challenge.getChallengeLayout(7);
        assert challenge7 != null;
        Board board7 = Board.fromChallenge(challenge7);

        // constructing and configuring caterpillar
        Caterpillar turquoise = Caterpillar.newTurquoise();
        turquoise.headDirection = Direction.UP;
        turquoise.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        turquoise.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);

        // creating positions to test trying to place the caterpillar
        Position correctPosition = new Position(1,0);
        Position noSpotPosition = new Position(2,1);
        Position tailDoesNotFit = new Position(4,0);

        // testing whether the caterpillar can be placed in the given positions
        assertFalse(board7.tryPlaceCaterpillar(turquoise,noSpotPosition));
        assertFalse(board7.tryPlaceCaterpillar(turquoise,tailDoesNotFit));
        assertTrue(board7.tryPlaceCaterpillar(turquoise,correctPosition));

        // testing whether the caterpillar was placed on the board in the correct places
        Spot[][] boardMatrix = board7.spotMatrix;
        assertSame(turquoise,boardMatrix[1][0].getOccupier(),
                "Turquoise caterpillar head segment was not placed on board in desired location");
        assertSame(turquoise,boardMatrix[1][1].getOccupier(),
                "Turquoise caterpillar second segment was not placed on board in desired location");
        assertSame(turquoise,boardMatrix[0][1].getOccupier(),
                "Turquoise caterpillar third segment was not placed on board in desired location");
        assertSame(turquoise,boardMatrix[0][2].getOccupier(),
                "Turquoise caterpillar fourth segment was not placed on board in desired location");
        assertSame(turquoise,boardMatrix[0][3].getOccupier(),
                "Turquoise caterpillar fifth segment was not placed on board in desired location");
        assertSame(turquoise,boardMatrix[0][4].getOccupier(),
                "Turquoise caterpillar sixth segment was not placed on board in desired location");

        // testing that the caterpillar isn't occupying board positions that it isn't meant to be
        assertNotSame(turquoise,boardMatrix[2][0].getOccupier(),
                "Turquoise caterpillar shouldn't occupy the position (2,0)");
        // There is no spot at positions (0,0) and (2,1) in this board configuration

    }


    /**
     * Tests the case where a caterpillar is placed onto the board in a valid position
     * and then the tryPlaceCaterpillar() method is attempted to be called again
     * for the same caterpillar and position. Also tests that a second caterpillar
     * can be placed in a different position to the first.
     *
     * ########### Caterpillar valid placement #############
     *    0  1  2  3  4
     * 0              y
     * 1              y
     * 2  g  g  g     y
     * 3  g    [g]    y
     * 4  g          [y]
     *
     * ########################################################
     */
    @Test
    void offOnBoardInPosition() {
        // setting up challenge and board
        String challenge8 =  Challenge.getChallengeLayout(8);
        assert challenge8 != null;
        Board board8 = Board.fromChallenge(challenge8);

        // constructing and configuring caterpillar
        Caterpillar green = Caterpillar.newGreen();
        green.headDirection = Direction.DOWN;
        green.setSegmentAngle(1, Segment.Pivot.LEFT_BEND);
        green.setSegmentAngle(3, Segment.Pivot.LEFT_BEND);

        // creating positions to test trying to place the caterpillar
        Position validPosition = new Position(2,3);
        Position offBoard = new Position(4,5);

        // testing whether the caterpillar can be placed in the given positions
        assertFalse(board8.tryPlaceCaterpillar(green,offBoard));
        assertTrue(board8.tryPlaceCaterpillar(green,validPosition));
        assertFalse(board8.tryPlaceCaterpillar(green,validPosition));

        // testing whether the caterpillar was placed on the board in the correct places
        Spot[][] boardMatrix = board8.spotMatrix;
        assertSame(green,boardMatrix[2][3].getOccupier(),
                "Green caterpillar head segment was not placed on board in desired location");
        assertSame(green,boardMatrix[2][2].getOccupier(),
                "Green caterpillar second segment was not placed on board in desired location");
        assertSame(green,boardMatrix[1][2].getOccupier(),
                "Green caterpillar third segment was not placed on board in desired location");
        assertSame(green,boardMatrix[0][2].getOccupier(),
                "Green caterpillar fourth segment was not placed on board in desired location");
        assertSame(green,boardMatrix[0][3].getOccupier(),
                "Green caterpillar fifth segment was not placed on board in desired location");
        assertSame(green,boardMatrix[0][4].getOccupier(),
                "Green caterpillar second segment was not placed on board in desired location");

        // creating a new yellow caterpillar to place in valid position
        Caterpillar yellow = Caterpillar.newYellow();
        yellow.headDirection = Direction.DOWN;
        Position yellowHeadPos = new Position(4,4);

        // testing that second caterpillar was added
        assertTrue(board8.tryPlaceCaterpillar(yellow,yellowHeadPos));
    }



    /**
     * Tests the case where the yellow caterpillar has been placed on the board in a valid
     * position and then the tryPlaceCaterpillar() method is called with the challenge
     * solution for the yellow caterpillar. Also tests if a second caterpillar cannot
     * be placed in a position where it overlaps with the first.
     *
     * ########### Yellow caterpillar valid placement #############
     *    0  1  2  3  4
     * 0             [y]
     * 1              y
     * 2              y
     * 3              y
     * 4              y
     *
     * ########################################################
     *
     * The below placement becomes un-placeable when the above placement is activated!
     ########### yellow caterpillar 'challengeSolution' placement #############
     *    0  1  2  3  4
     * 0
     * 1
     * 2  y  y [y]
     * 3  y
     * 4  y
     * #######################################################
     *
     *
     * The following placement overlaps with the first yellow caterpillar placement
     * ########### conflicting green Caterpillar placement #############
     *    0  1  2  3  4
     * 0          [g] g
     * 1              g
     * 2 [            g
     * 3              g
     * 4              g
     * #######################################################
     */
    @Test
    void offOnBoardDifferentPositionAndOccupation() {
        // setting up challenge and board
        String challenge6 =  Challenge.getChallengeLayout(6);
        assert challenge6 != null;
        Board board6 = Board.fromChallenge(challenge6);

        // constructing and configuring caterpillar
        Caterpillar yellow = Caterpillar.newYellow();
        yellow.headDirection = Direction.UP;
        assert !yellow.isPlaced();

        // creating positions to test trying to place the caterpillar
        Position validPosition = new Position(4,0);
        Position offBoard = new Position(Board.BOARD_WIDTH,Board.BOARD_HEIGHT+1);
        Position noSpotPositionOne = new Position(1,3);
        Position noSpotPositionTwo = new Position(3,2);
        Position endOfTailOffBoard = new Position(2,0);


        // testing whether the caterpillar can be placed in the given positions
        assertFalse(board6.tryPlaceCaterpillar(yellow,offBoard));
        assertFalse(board6.tryPlaceCaterpillar(yellow,noSpotPositionOne));
        assertFalse(board6.tryPlaceCaterpillar(yellow,noSpotPositionTwo));
        assertFalse(board6.tryPlaceCaterpillar(yellow,endOfTailOffBoard));
        assertTrue(
                board6.tryPlaceCaterpillar(yellow,validPosition));

        // testing whether the caterpillar was placed on the board in the correct places
        Spot[][] boardMatrix = board6.spotMatrix;
        assertSame(yellow,boardMatrix[4][0].getOccupier(),
                "Yellow caterpillar head segment was not placed on board in desired location");
        assertSame(yellow,boardMatrix[4][1].getOccupier(),
                "Yellow caterpillar second segment was not placed on board");
        assertSame(yellow,boardMatrix[4][2].getOccupier(),
                "Yellow caterpillar third segment was not placed on board");
        assertSame(yellow,boardMatrix[4][3].getOccupier(),
                "Yellow caterpillar fourth segment was not placed on board");
        assertSame(yellow,boardMatrix[4][4].getOccupier(),
                "Yellow caterpillar fifth segment was not placed on board");

        // creating another placement of the same caterpillar
        yellow.headDirection = Direction.RIGHT;
        Position challengeSolutionPosition = new Position(2,2);
        assert yellow.isPivot(2);
        // creating left bend at third segment (index 2)
        yellow.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);

        // tests that caterpillar is not placed after it already has been placed and
        // is occupying spots on the board.
        assertFalse(
                board6.tryPlaceCaterpillar(yellow,challengeSolutionPosition));


        // creating new caterpillar and attempting to place it where it overlaps
        // with the yellow caterpillar that is already on the board
        Caterpillar green = Caterpillar.newGreen();
        Position greenConflictingPos = new Position(3,0);
        green.headDirection = Direction.LEFT;
        green.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        assertFalse(
                board6.tryPlaceCaterpillar(green,greenConflictingPos));
    }




}
