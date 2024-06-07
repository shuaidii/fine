package comp1110.ass1;

import comp1110.ass1.caterpillar.Caterpillar;
import comp1110.ass1.caterpillar.Colour;
import comp1110.ass1.caterpillar.Direction;
import comp1110.ass1.caterpillar.Segment;
import comp1110.ass1.challenge.Challenge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public class ChallengeSolvedTest {
    Caterpillar extractColour(Caterpillar[] caterpillars, Colour colour) {
        return Arrays.stream(caterpillars)
                .filter(c -> c.getColour().equals(colour))
                .findFirst().get();
    }

    @Test
    void starter() {
        AppleTwist puzzle = new AppleTwist(Challenge.challengeFromNumber(7));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        Caterpillar cat = extractColour(puzzle.getCaterpillars(), Colour.YELLOW);
        cat.headDirection = Direction.DOWN;
        cat.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(4, 2)));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        cat = extractColour(puzzle.getCaterpillars(), Colour.TURQUOISE);
        cat.headDirection = Direction.UP;
        cat.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        cat.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(1, 0)));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        cat = extractColour(puzzle.getCaterpillars(), Colour.GREEN);
        cat.headDirection = Direction.LEFT;
        cat.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        cat.setSegmentAngle(3, Segment.Pivot.LEFT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(1, 2)));
        Assertions.assertTrue(puzzle.isPuzzleSolved());
    }

    @Test
    void junior() {
        AppleTwist puzzle = new AppleTwist(Challenge.challengeFromNumber(22));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        Caterpillar cat = extractColour(puzzle.getCaterpillars(), Colour.YELLOW);
        cat.headDirection = Direction.RIGHT;
        cat.setSegmentAngle(2, Segment.Pivot.RIGHT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(3, 4)));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        cat = extractColour(puzzle.getCaterpillars(), Colour.TURQUOISE);
        cat.headDirection = Direction.LEFT;
        cat.setSegmentAngle(1, Segment.Pivot.LEFT_BEND);
        cat.setSegmentAngle(2, Segment.Pivot.RIGHT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(0, 1)));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        cat = extractColour(puzzle.getCaterpillars(), Colour.GREEN);
        cat.headDirection = Direction.DOWN;
        cat.setSegmentAngle(1, Segment.Pivot.LEFT_BEND);
        cat.setSegmentAngle(3, Segment.Pivot.RIGHT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(4, 4)));
        Assertions.assertTrue(puzzle.isPuzzleSolved());
    }

    @Test
    void expert() {
        AppleTwist puzzle = new AppleTwist(Challenge.challengeFromNumber(39));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        Caterpillar cat = extractColour(puzzle.getCaterpillars(), Colour.YELLOW);
        cat.headDirection = Direction.DOWN;
        cat.setSegmentAngle(1, Segment.Pivot.LEFT_BEND);
        cat.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(1, 1)));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        cat = extractColour(puzzle.getCaterpillars(), Colour.TURQUOISE);
        cat.headDirection = Direction.UP;
        cat.setSegmentAngle(1, Segment.Pivot.LEFT_BEND);
        cat.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(1, 3)));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        cat = extractColour(puzzle.getCaterpillars(), Colour.GREEN);
        cat.headDirection = Direction.RIGHT;
        cat.setSegmentAngle(1, Segment.Pivot.RIGHT_BEND);
        cat.setSegmentAngle(3, Segment.Pivot.STRAIGHT);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(4, 4)));
        Assertions.assertTrue(puzzle.isPuzzleSolved());
    }

    @Test
    void master() {
        AppleTwist puzzle = new AppleTwist(Challenge.challengeFromNumber(53));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        Caterpillar cat = extractColour(puzzle.getCaterpillars(), Colour.YELLOW);
        cat.headDirection = Direction.LEFT;
        cat.setSegmentAngle(1, Segment.Pivot.LEFT_BEND);
        cat.setSegmentAngle(2, Segment.Pivot.LEFT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(2, 2)));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        cat = extractColour(puzzle.getCaterpillars(), Colour.TURQUOISE);
        cat.headDirection = Direction.RIGHT;
        cat.setSegmentAngle(1, Segment.Pivot.LEFT_BEND);
        cat.setSegmentAngle(2, Segment.Pivot.STRAIGHT);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(1, 0)));
        Assertions.assertFalse(puzzle.isPuzzleSolved());

        cat = extractColour(puzzle.getCaterpillars(), Colour.GREEN);
        cat.headDirection = Direction.UP;
        cat.setSegmentAngle(1, Segment.Pivot.LEFT_BEND);
        cat.setSegmentAngle(3, Segment.Pivot.LEFT_BEND);
        Assertions.assertTrue(puzzle.getBoard().tryPlaceCaterpillar(cat, new Position(2, 3)));
        Assertions.assertTrue(puzzle.isPuzzleSolved());
    }
}
