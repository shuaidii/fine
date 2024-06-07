package comp1110.ass1.caterpillar;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class SegmentDirectionsTest {

    // For the documentation in this class:
    // ###### KEY / LEGEND #######
    // "g | y | t" is a green or yellow or turquoise segment of caterpillar
    // "[g | y | t]" => head of a green or yellow or turquoise caterpillar
    // ###########################


    /**
     * Case of straight vertical caterpillar
     * #############
     *
     *  [g]
     *   g
     *   g
     *   g
     *   g
     *   g
     *
     * #############
     */
    @Test
    void straightDownwardCaterpillar() {
        Caterpillar greenCaterpillar = Caterpillar.newGreen();
        greenCaterpillar.headDirection = Direction.DOWN;
        Direction[] directions = greenCaterpillar.getDirections();
        Direction[] expectedDirections = new Direction[]
                {Direction.DOWN,Direction.DOWN,Direction.DOWN,Direction.DOWN,Direction.DOWN,Direction.DOWN};
        assertArrayEquals(expectedDirections,directions,
                "Given direction of segment did not match expected direction: ");
    }


    /**
     * case of straight horizontal caterpillar
     *#############
     *
     * y  y  y  y  y [y]
     *
     *#############
     */
    @Test
    void straightSidewaysCaterpillar(){
        Caterpillar yellowCaterpillar = Caterpillar.newYellow();
        yellowCaterpillar.headDirection = Direction.RIGHT;
        Direction[] directions = yellowCaterpillar.getDirections();
        Direction[] expectedDirections = new Direction[]
                {Direction.RIGHT,Direction.RIGHT,Direction.RIGHT,Direction.RIGHT,Direction.RIGHT};
        assertArrayEquals(expectedDirections,directions,
                "Given direction of segment did not match expected direction: ");
    }


    /**
     * case of caterpillar with one right bend
     *#############
     *
     * [t] t
     *     t
     *     t
     *     t
     *     t
     *
     *#############
     */
    @Test
    void rightBendCaterpillar(){
        Caterpillar turquoiseCaterpillar = Caterpillar.newTurquoise();
        turquoiseCaterpillar.headDirection = Direction.LEFT;
        turquoiseCaterpillar.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        Direction[] directions = turquoiseCaterpillar.getDirections();
        Direction[] expectedDirections = new Direction[]
                {Direction.LEFT,Direction.LEFT,Direction.UP,Direction.UP,Direction.UP,Direction.UP};
        assertArrayEquals(expectedDirections,directions,
                "Given direction of segment did not match expected direction: ");
    }


    /**
     * case of caterpillar with one left bend
     *#############
     *
     *         t
     *         t
     *         t
     *  [t] t  t
     *
     *#############
     */
    @Test
    void leftBendCaterpillar(){
        Caterpillar turquoiseCaterpillar = Caterpillar.newTurquoise();
        turquoiseCaterpillar.headDirection = Direction.LEFT;
        turquoiseCaterpillar.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);
        Direction[] directions = turquoiseCaterpillar.getDirections();
        Direction[] expectedDirections = new Direction[]
                {Direction.LEFT,Direction.LEFT,Direction.LEFT,Direction.DOWN,Direction.DOWN,Direction.DOWN};
        assertArrayEquals(expectedDirections,directions,
                "Given direction of segment did not match expected direction: ");
    }


    /**
     * case of caterpillar with two of the same bend
     *#############
     *
     *   g  g  g
     *  [g]    g
     *         g
     *
     *#############
     */
    @Test
    void twoRightBendCaterpillar(){
        Caterpillar greenCaterpillar = Caterpillar.newGreen();
        greenCaterpillar.headDirection = Direction.DOWN;
        greenCaterpillar.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        greenCaterpillar.setSegmentAngle(3, Segment.Pivot.RIGHT_BEND);
        Direction[] directions = greenCaterpillar.getDirections();
        Direction[] expectedDirections = new Direction[]
                {Direction.DOWN,Direction.DOWN,Direction.LEFT,Direction.LEFT,Direction.UP,Direction.UP};
        assertArrayEquals(expectedDirections,directions,
                "Given direction of segment did not match expected direction: ");
    }


    /**
     * case of a caterpillar with two different bends
     *#############
     *
     *        g
     *        g
     *  g  g  g
     * [g]
     *
     *#############
     */
    @Test
    void rightLeftBendCaterpillar(){
        Caterpillar greenCaterpillar = Caterpillar.newGreen();
        greenCaterpillar.headDirection = Direction.DOWN;
        greenCaterpillar.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        greenCaterpillar.setSegmentAngle(3, Segment.Pivot.LEFT_BEND);
        Direction[] directions = greenCaterpillar.getDirections();
        Direction[] expectedDirections = new Direction[]
                {Direction.DOWN,Direction.DOWN,Direction.LEFT,Direction.LEFT,Direction.DOWN,Direction.DOWN};
        assertArrayEquals(expectedDirections,directions,
                "Given direction of segment did not match expected direction: ");
    }





}
