package GAME;

//description of class
// The Position class represents a location on Blue Lagoon game board.
// It contains two integers representing rows and columns of position on the board.
// The Position class also includes methods to get and set rows and columns.
// Every odd numbered row has 12 columns and every even numbered row has 13 columns (162 total spaces).

import java.util.*;

public class Position {
    public static Position[][] positions = {}; // position x and y coordinates

    private int x;
    private int y;
    public boolean island = false;
    public Set<String> resources = new HashSet<>();
    public Player owner = null;

    public Position(int x, int y, boolean island) {
        this.x = x;
        this.y = y;
        this.island = island;
    }

    public static Position getPosition(int x, int y) {

        return Position.positions[x][y];
    } // return x and y coordinates of a position

    public static void setPosition() {
    } // set x and y coordinates

    public static List<Position> getNeighbors(Position position) {
        List<Position> list = new ArrayList<>();
        int x = position.x;
        int y = position.y;
        if (x - 1 > 0) {
            list.add(positions[x - 1][y]);
        }
        if (x + 1 < positions.length) {
            list.add(positions[x + 1][y]);
        }
        if (y - 1 >= 0) {
            list.add(positions[x][y]);
        }
        if (y + 1 < positions[0].length) {
            list.add(positions[x][y + 1]);
        }
        list.removeIf(positionTmp -> positionTmp.owner != null);
        return list;
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

    public Player getOwner() {
        return owner;
    }
}
