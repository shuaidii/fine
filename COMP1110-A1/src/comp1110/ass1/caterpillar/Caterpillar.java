package comp1110.ass1.caterpillar;


import comp1110.ass1.Position;

import java.util.Arrays;

/**
 * Caterpillar with rotating pivots.
 *
 * The position of the caterpillar on the board.
 // completely defines the position of a caterpillar on the board and it's associated constraints
 */
public class Caterpillar {
    private final Colour colour;
    final Segment[] segments;
    public Direction headDirection = Direction.UP;
    private Position headPosition = new Position(-1, -1);

    public Caterpillar(Colour colour, Segment[] segments) {
        assert colour != Colour.WILDCARD; // only constraints can have wildcard constraints
        this.colour = colour;
        this.segments = segments;
    }

    public static Caterpillar newGreen() {
        Segment[] segments = {
                new Segment(false),
                new Segment(true),
                new Segment(false),
                new Segment(true),
                new Segment(false),
                new Segment(false)
        };
        return new Caterpillar(Colour.GREEN, segments);
    }
    public static Caterpillar newYellow() {
        Segment[] segments = {
                new Segment(false),
                new Segment(true),
                new Segment(true),
                new Segment(false),
                new Segment(false)
        };
        return new Caterpillar(Colour.YELLOW, segments);
    }

    public static Caterpillar newTurquoise() {
        Segment[] segments = {
                new Segment(false),
                new Segment(true),
                new Segment(true),
                new Segment(false),
                new Segment(false),
                new Segment(false)
        };
        return new Caterpillar(Colour.TURQUOISE, segments);
    }

    /**
     * @return colour of the caterpillar
     */
    public Colour getColour() {
        return this.colour;
    }

    /**
     * @return number of caterpillar segments
     */
    public int getLength() {
        return segments.length;
    }

    /**
     * @return the board position of the caterpillar head, or -1, -1 if off the
     *         board.
     */
    public Position getHeadPosition() {
        return headPosition;
    }

    /**
     * Set position of caterpillar head
     *
     * Can be a valid board position, or -1, -1 for off the board.
     * @param pos new position of the caterpillar head
     */
    public void setHeadPosition(Position pos) {
        headPosition = pos;
    }

    /**
     * @return true if the caterpillar placed on the board (apple)
     */
    public boolean isPlaced() {
        // Could be made more robust
        return headPosition.getX() != -1 && headPosition.getY() != -1;
    }

    /**
     * Rotate the whole caterpillar (counter-clockwise)
     *
     * The whole caterpillar is rotated by changing the head
     * direction (the pivots are left alone).
     *
     * E.g., if the headDirection is LEFT: after calling this method
     * it will be DOWN, and on a second call it will be RIGHT, and so on.
     */
    public void rotate() {
        // // FIXME Task 4
        switch (headDirection) {
            case LEFT -> headDirection = Direction.DOWN;
            case DOWN -> headDirection = Direction.RIGHT;
            case RIGHT -> headDirection = Direction.UP;
            case UP -> headDirection = Direction.LEFT;
        }
    }

    public void setSegmentAngle(int segmentIndex, Segment.Pivot pivot) {
        assert segments[segmentIndex].canPivot();
        segments[segmentIndex].setPivotAngle(pivot);
    }

    /**
     * Cycle through pivot angles for segment at index
     *
     * This has no effect if the segment is not a pivot.
     * @param segmentIndex 0, ..., getLength() - 1
     */
    public void nextPivotAngle(int segmentIndex) {
        segments[segmentIndex].nextAngle();
    }

    /**
     * Direction from each segment to its previous segment
     *
     * For a given segment, the previous segment is defined as the segment
     * one closer to head of the caterpillar, along the caterpillar's body.
     *
     * For the caterpillar head itself, the direction it is pointing is
     * provided.
     *
     * These directions should factor in the head rotation of the caterpillar
     * and the state of any pivots.
     *
     * E.g., the following caterpillar has a head pointing LEFT:
     *
     *  H P P S S     (H = head, P = pivot, S = regular segment)
     *
     * and this method will return [LEFT, LEFT, LEFT, LEFT, LEFT].
     *
     * The following caterpillar has a head pointing UP:
     *
     *       S
     *     H S
     *     P P
     *
     * and this method will return [UP, UP, LEFT, DOWN, DOWN]
     *
     * @return direction from each segment to its previous segment
     */
    public Direction[] getDirections() {
        Direction[] directions = new Direction[segments.length]; // FIXME
        directions[0] = headDirection;
        for (int i = 1; i < segments.length; i++) {
            Direction tailDirection = segments[i - 1].tailDirection(directions[i - 1]);
            directions[i] = tailDirection.reverse();
        }
        return directions; // FIXME Task 6
    }

    /**
     * Segment positions relative to virtualHead
     *
     * First segment is the head, so will have position equal to virtualHead.
     *
     * E.g., consider the following caterpillar with head pointing LEFT:
     *
     *  H P P S S     (H = head, P = pivot, S = regular segment)
     *
     * If a virtualHead of (0, 0) is provided, then this method will return
     * [(0, 0), (1, 0), (2, 0), (3, 0), (4, 0)].
     *
     * If a virtualHead of (-5, 3) is provided, then this method will return
     * [(-5, 3), (-4, 3), (-3, 3), (-2, 3), (-1, 3)].
     *
     * Consider the following caterpillar with head pointing UP:
     *
     *       S
     *     H S
     *     P P
     *
     * If a virtualHead of (0, 0) is provided, then this method will return
     * [(0, 0), (0, 1), (1, 1), (1, 0), (1, -1)].
     *
     * If a virtualHead of (6, -3) is provided, then this method will return
     * [(6, -3), (6, -2), (7, -2), (7, -3), (7, -4)].
     *
     * If the caterpillar is validly placed and the actual caterpillar board
     * position of the head is provided, this will return valid board positions
     * for each segment.
     * @param virtualHead head position to use as reference
     * @return segment integer positions relative to virtualHead position
     */
    public Position[] getPositions(Position virtualHead) {
        Position[] positions = new Position[segments.length]; // FIXME
        Direction[] directions = getDirections();
        positions[0] = virtualHead;
        for (int i = 1; i < segments.length; i++) {
            Position prePos = positions[i - 1];
            Direction direction = directions[i];
            positions[i] = prePos.add(direction.reverse().toUnitVector());
        }

        return positions; // FIXME Task 7
    }

    /**
     * Segment positions relative to actual caterpillar head
     *
     * The first segment will be the position of the head itself.
     * The positions are only valid board positions if the caterpillar is
     * validly placed.
     * @return segment integer positions relative to head position
     */
    public Position[] getPositions() {
        return getPositions(headPosition);
    }

    /**
     * Is the segment with provided index a pivot segment
     * @param segmentIndex 0, ..., getLength() - 1
     * @return true if pivot segment
     */
    public boolean isPivot(int segmentIndex) {
        return segments[segmentIndex].canPivot();
    }
}
