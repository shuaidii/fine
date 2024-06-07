package comp1110.ass1;

import java.util.Objects;

/**
 * A two-dimensional integer position
 */
public class Position {
    final int x;
    final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Add two positions
     *
     * E.g., Position(2, 3).add(Position(-4, 5)) is equal to Position(-2, 8)
     *
     * @param other Position to add to this Position
     * @return a new Position which is the element-wise addition of this and other
     */
    public Position add(Position other) {
        return new Position(x + other.x, y + other.y); // FIXME Task 2
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }
}
