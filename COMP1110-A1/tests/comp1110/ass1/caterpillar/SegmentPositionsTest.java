package comp1110.ass1.caterpillar;

import comp1110.ass1.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;


@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class SegmentPositionsTest {

    // For the documentation in this class:
    // ###### KEY / LEGEND #######
    // "g | y | t" is a green or yellow or turquoise segment of caterpillar
    // "[g | y | t]" => head of a green or yellow or turquoise caterpillar
    // ###########################


    /**
     * Converts an array of ints into positions by splitting them up into
     * pairs of ints.
     * @param combinedPositionCoordinates int array in form of [x1,y1,x2,y2,...,xn,yn]
     * @return array of Position objects
     */
    private Position[] posCon(int[] combinedPositionCoordinates) {
        assert combinedPositionCoordinates.length % 2 ==0;
        Position[] positions = new Position[combinedPositionCoordinates.length/2];
        for (int i=0; i<combinedPositionCoordinates.length; i=i+2) {
            positions[i/2] = new Position(combinedPositionCoordinates[i],
                    combinedPositionCoordinates[i+1]);
        }
        return positions;
    }




    /**
     * Case of straight vertical caterpillar
     * ############################
     *   0  1  2  3  4
     * 0
     * 1   [g]
     * 2    g
     * 3    g
     * 4    g
     * 5    g
     * 6    g
     * ############################
     * Note that the below grid is larger than the game board but this detail
     * doesn't matter for the purposes of this unit test.
     */
    @Test
    void straightUprightCaterpillar() {
        Caterpillar greenCaterpillar = Caterpillar.newGreen();
        greenCaterpillar.headDirection = Direction.UP;
        greenCaterpillar.setHeadPosition(new Position(1,1));
        Position[] positions = greenCaterpillar.getPositions();
        Position[] expectedPositions = posCon(new int[]{1,1,1,2,1,3,1,4,1,5,1,6});
        Assertions.assertArrayEquals(expectedPositions,positions,
                "Given position did not match expected position: ");
    }


    /**
     * case of straight horizontal caterpillar
     * ############################
     *    -1  0  1  2  3  4
     * 6
     * 7   y  y  y  y  y [y]
     * 8
     * ############################
     * Note that the below grid is larger than the game board but this detail
     * doesn't matter for the purposes of this unit test.
     */
    @Test
    void straightSidewaysCaterpillar(){
        Caterpillar yellowCaterpillar = Caterpillar.newYellow();
        yellowCaterpillar.headDirection = Direction.RIGHT;
        yellowCaterpillar.setHeadPosition(new Position(3,7));
        Position[] positions = yellowCaterpillar.getPositions();
        Position[] expectedPositions = posCon(new int[]{3,7,2,7,1,7,0,7,-1,7});
        Assertions.assertArrayEquals(expectedPositions,positions,
                "Given position did not match expected position: ");
    }


    /**
     *  case of caterpillar with one right bend
     * ############################
     *    0  1  2  3  4
     * 1
     * 2       [t] t
     * 3           t
     * 4           t
     * 5           t
     * 6           t
     * 7
     *############################
     * Note that the below grid is larger than the game board but this detail
     * doesn't matter for the purposes of this unit test.
     */
    @Test
    void rightBendCaterpillar(){
        Caterpillar turquoiseCaterpillar = Caterpillar.newTurquoise();
        turquoiseCaterpillar.headDirection = Direction.LEFT;
        turquoiseCaterpillar.setHeadPosition(new Position(2,2));
        turquoiseCaterpillar.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        Position[] positions = turquoiseCaterpillar.getPositions();
        Position[] expectedPositions = posCon(new int[]{2,2,3,2,3,3,3,4,3,5,3,6});
        Assertions.assertArrayEquals(expectedPositions,positions,
                "Given position did not match expected position: ");
    }


    /**
     * ase of caterpillar with one left bend
     * ############################
     *    0  1  2  3  4
     * -3
     * -2          t
     * -1          t
     *  0          t
     *  1   [t] t  t
     *  2
     * ############################
     * Note that the below grid is larger than the game board but this detail
     * doesn't matter for the purposes of this unit test.
     */
    @Test
    void leftBendCaterpillar(){
        Caterpillar turquoiseCaterpillar = Caterpillar.newTurquoise();
        turquoiseCaterpillar.headDirection = Direction.LEFT;
        turquoiseCaterpillar.setHeadPosition(new Position(1,1));
        turquoiseCaterpillar.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);
        Position[] positions = turquoiseCaterpillar.getPositions();
        Position[] expectedPositions = posCon(new int[]{1,1,2,1,3,1,3,0,3,-1,3,-2});
        Assertions.assertArrayEquals(expectedPositions,positions,
                "Given position did not match expected position: ");
    }

    /**
     * case of caterpillar with two of the same bend
     * ############################
     *    5  6  7
     * 2
     * 3  g  g  g
     * 4 [g]    g
     * 5        g
     * 6
     * ############################
     * Note that the below grid is larger than the game board but this detail
     * doesn't matter for the purposes of this unit test.
     */
    @Test
    void twoRightBendCaterpillar(){
        Caterpillar greenCaterpillar = Caterpillar.newGreen();
        greenCaterpillar.headDirection = Direction.DOWN;
        greenCaterpillar.setHeadPosition(new Position(5,4));
        greenCaterpillar.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        greenCaterpillar.setSegmentAngle(3, Segment.Pivot.RIGHT_BEND);
        Position[] positions = greenCaterpillar.getPositions();
        Position[] expectedPositions = posCon(new int[]{5,4,5,3,6,3,7,3,7,4,7,5});
        Assertions.assertArrayEquals(expectedPositions,positions,
                "Given position did not match expected position: ");
    }


    /**
     * case of a caterpillar with two different bends
     * ############################
     *   0  1  2  3  4  5
     * 0                g
     * 1                g
     * 2          g  g  g
     * 3         [g]
     * 4
     * ###########################
     * Note that the below grid is larger than the game board but this detail
     * doesn't matter for the purposes of this unit test.
     */

    @Test
    void rightLeftBendCaterpillar(){
        Caterpillar greenCaterpillar = Caterpillar.newGreen();
        greenCaterpillar.headDirection = Direction.DOWN;
        greenCaterpillar.setHeadPosition(new Position(3,3));
        greenCaterpillar.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        greenCaterpillar.setSegmentAngle(3, Segment.Pivot.LEFT_BEND);
        Position[] positions = greenCaterpillar.getPositions();
        Position[] expectedPositions = posCon(new int[]{3,3,3,2,4,2,5,2,5,1,5,0});
        Assertions.assertArrayEquals(expectedPositions,positions,
                "Given position did not match expected position: ");
    }






}






















