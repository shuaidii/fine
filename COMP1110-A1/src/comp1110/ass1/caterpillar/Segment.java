package comp1110.ass1.caterpillar;

public class Segment {

    /**
     * Possible pivot rotations.
     *
     * LEFT_BEND and RIGHT_BEND correspond to pivots that have been bent
     * to be at a right angle. The reference for LEFT_BEND and RIGHT_BEND
     * is starting at the head and looking towards the tail of the caterpillar.
     *
     * The angle variable corresponds to the angle between the segments either
     * side of the pivot.
     * E.g., 180 degrees represents 3 segments in a straight line, with
     * the pivot segment in the middle.
     */
    public enum Pivot {
        LEFT_BEND(90),
        STRAIGHT(180),
        RIGHT_BEND(270);

        final int angle;
        Pivot(int angle) {
            this.angle = angle;
        }
    }

    // Whether segment can pivot
    private final boolean canPivot;

    // Used for pivots and non pivot segments. 180 degrees is used for
    // non-pivots as all non-pivots are in a straight line connection
    // with their previous and next segment.
    private Pivot pivot;

    public Segment(boolean canPivot) {
        this.canPivot = canPivot;
        this.pivot = Pivot.STRAIGHT;
    }

    /**
     * @return whether the segment can pivot
     */
    public boolean canPivot() {
        return canPivot;
    }

    /**
     * Cycle through possible pivot rotations
     *
     * No effect if the segment is not a pivot.
     */
    public void nextAngle() {
        if (canPivot) {
            pivot = switch (pivot) {
                case LEFT_BEND -> Pivot.STRAIGHT;
                case STRAIGHT -> Pivot.RIGHT_BEND;
                case RIGHT_BEND -> Pivot.LEFT_BEND;
            };
        }
    }

    /**
     *
     * @param pivot Angle of the
     */
    public void setPivotAngle(Pivot pivot) {
        this.pivot = pivot;
    }

    /**
     * Get the direction to the next segment (towards tail) after this
     *
     * This works for all segments. The direction of the previous segment
     * in the direction of the caterpillar head is required in order to
     * be able to establish an absolute direction that factors in the
     * caterpillar head rotation and any upstream pivot rotations.
     * @param headDirection direction from this segment towards neighbouring
     *                      segment on path to the caterpillar head
     * @return direction from this segment towards neighbouring segment
     *         on path to tail
     */
    public Direction tailDirection(Direction headDirection) {
        int angle = headDirection.angle + pivot.angle;
        return Direction.getDirectionFromAngle(angle);
    }
}

