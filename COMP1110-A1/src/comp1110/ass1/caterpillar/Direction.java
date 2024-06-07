package comp1110.ass1.caterpillar;

import comp1110.ass1.Position;

/**
 * One of the 4 cardinal directions in 2D space
 */
public enum Direction {
    RIGHT(0), // in positive x direction
    UP(270), // in negative y direction
    LEFT(180), // in negative x direction
    DOWN(90); // in positive y direction
    public final int angle;

    Direction(int angle) {
        this.angle = angle;
    }

    /**
     * Convert angle in degrees into a cardinal direction
     *
     * @param angle in degrees must be non-negative, can be over 360
     * @return new cardinal direction if angle divisible by 90, else null
     */
    public static Direction getDirectionFromAngle(int angle) {
        assert angle >= 0;
        angle = angle % 360;
        for (Direction direction : Direction.values()) {
            if (direction.angle == angle) {
                return direction;
            }
        }
        return null;
    }

    /**
     * @return new direction that is in opposite direction to this one
     */
    public Direction reverse() {
        return Direction.getDirectionFromAngle(this.angle + 180);
    }

    /**
     * Get unit vector representation of direction
     *
     * @return position that represents unit vector of direction
     */
    public Position toUnitVector() {
        int x = 0;
        int y = 0;
        switch (this) {
            case RIGHT -> x = 1;
            case UP -> y = -1;
            case LEFT -> x = -1;
            case DOWN -> y = 1;
        }
        return new Position(x, y);
    }
}
