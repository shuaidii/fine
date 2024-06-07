package comp1110.ass1.board;

import comp1110.ass1.caterpillar.Caterpillar;
import comp1110.ass1.caterpillar.Colour;
import comp1110.ass1.Position;
import comp1110.ass1.challenge.RowTemplate;

/**
 * Representation of the puzzle board (apple) state.
 *
 * The state includes the Spot layout of the current challenge and their occupation.
 */
public class Board {
    // The width of the board (left to right)
    public final static int BOARD_WIDTH = 5;

    // The height of the board (top to bottom)
    public final static int BOARD_HEIGHT = 5;

    // The matrix of spots representing the puzzle board
    // For spotMatrix[x][y]:
    //   x corresponds to the apple column, working left to right, and
    //   y corresponds to the apple row, working top to bottom.
    // Values default to null where there are no spots
    final Spot[][] spotMatrix =  new Spot[BOARD_WIDTH][BOARD_HEIGHT];

    private Board() {
    }

    /**
     * Add spots that correspond to provided challenge
     *
     * The boardEncoding must be BOARD_HEIGHT characters long, and
     * each character must be one of [12345ABCDE].
     *
     * @param boardEncoding string encoding of apple rows to use in challenge
     * @throws IllegalArgumentException if boardEncoding is not valid
     */
    private void addSpots(String boardEncoding) {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            char rowChar = boardEncoding.charAt(y);
            RowTemplate row = RowTemplate.decodeRow(rowChar);
            if (row == null) {
                throw new IllegalArgumentException(
                        "Invalid spot positions in challenge row encoding, row / index "
                                + y + " of: " + boardEncoding);
            }
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (row.getRow()[x]) { // positions without a spot will remain null
                    spotMatrix[x][y] = new Spot();
                }
            }
        }
    }

    /**
     * Set spot requirements that correspond to provided challenge
     *
     * reqEncoding must have a length divisible by 3 (including zero length).
     * Each consecutive group of 3 characters encodes a spot requirement:
     *
     * The first character in each block is a character 'T', 'G', 'Y', or 'X'
     * that corresponds to a caterpillar colour ('X' stands for a wildcard
     * that any caterpillar can satisfy).
     *
     * The next two characters are digits from the set '0', '1', ... '4',
     * corresponding to the x (column) and y (row) position respectively of
     * the spot that the requirement applies to.
     *
     * E.g., "G04X11" represents two requirements:
     *
     * The first requires the spot at column 0, row 4 to have the head of
     * the green caterpillar.
     *
     * The second requires the spot at column 1, row 1 to have the head of
     * any caterpillar (the colour doesn't matter).
     *
     * Note that columns increase left to right and rows increase top to
     * bottom.
     *
     * Hint: make use of the Spot class as it already has a method
     * for storing a requirement.
     *
     * @param reqEncoding string encoding of spot caterpillar head requirements
     * @throws Exception (various) if it fails to parse
     */
    private void addRequirements(String reqEncoding) {
        // // FIXME Task 5
        for (int i = 0; i < reqEncoding.length(); i += 3) {
            Colour colour = Colour.fromChar(reqEncoding.charAt(i));
            int column = Integer.parseInt(reqEncoding.substring(i + 1, i + 2));
            int row = Integer.parseInt(reqEncoding.substring(i + 2, i + 3));

            Spot spot = spotMatrix[column][row];
            spot.setRequiredHeadColour(colour);
        }
    }

    /**
     * Create a new board that reflects the provided challenge.
     *
     * @param challengeLayout string encoding
     * @return board configured for challenge
     * @throws IllegalArgumentException if it cannot parse challenge
     *         (possible also various other exceptions)
     */
    public static Board fromChallenge(String challengeLayout) {
        Board board = new Board();
        board.addSpots(challengeLayout.substring(0, BOARD_HEIGHT));
        board.addRequirements(challengeLayout.substring(BOARD_HEIGHT));
        return board;
    }

    /**
     * Check if all positions are spots that are unoccupied
     *
     * A position is unoccupied iff:
     *   - it is a valid board position,
     *   - there is a spot there, and
     *   - that spot is not occupied by any caterpillar.
     * @param positions to check are unoccupied
     * @return true if all positions have a spot that is unoccupied
     */
    boolean arePositionsUnoccupied(Position[] positions) {
        for (Position position : positions) {
            if (position.getX() < 0 || position.getX() >= BOARD_WIDTH
                    || position.getY() < 0 || position.getY() >= BOARD_HEIGHT) {
                return false;
            }
            Spot spot = spotMatrix[position.getX()][position.getY()];
            if (spot == null || spot.isOccupied()) {
                return false;
            }
        }
        return true; // FIXME Task 8
    }

    /**
     * Attempt to place caterpillar on board
     *
     * In order for the caterpillar to be placed, all segments of the
     * caterpillar must have valid board positions that contain a spot that
     * is currently unoccupied.
     *
     * This method does nothing and returns false if caterpillar is already
     * placed on the board at this or any other position.
     * If the caterpillar cannot be placed, then the board and caterpillar will
     * not be modified.
     * If the caterpillar is placed, the head position of the caterpillar is
     * modified and the relevant board spots are occupied.
     * @param caterpillar to add to board (head position is mutated if successful)
     * @param headPos board position place caterpillar and set caterpillar head to
     * @return true for success, false for invalid placement
     */
    public boolean tryPlaceCaterpillar(Caterpillar caterpillar, Position headPos) {
        if (caterpillar.isPlaced()) {
            return false;
        }
        Position[] positions = caterpillar.getPositions(headPos);
        if (!arePositionsUnoccupied(positions)) {
            return false;
        }
        for (Position position : positions) {
            Spot spot = spotMatrix[position.getX()][position.getY()];
//            spot.setRequiredHeadColour();
            spot.occupy(caterpillar);
        }
        caterpillar.setHeadPosition(headPos);
        return true; // FIXME Task 8
    }

    /**
     * Remove caterpillar from the board
     *
     * In addition to removing the caterpillar from spots it occupies, the
     * head position of the caterpillar is set to off the board.
     * Provided caterpillar must be same object as originally added.
     * @param caterpillar to remove (head position will be mutated)
     */
    public void removeCaterpillar(Caterpillar caterpillar) {
        for (int x = 0; x < Board.BOARD_WIDTH; x++) {
            for (int y = 0; y < Board.BOARD_HEIGHT; y++) {
                Spot spot = spotMatrix[x][y];
                if (spot == null ) {
                    continue;
                }
                if (spot.getOccupier() == caterpillar) {
                    spot.unoccupy();
                }
            }
        }
        caterpillar.setHeadPosition(new Position(-1, -1));
    }

    /**
     * Head colour requirement for provided spot position
     *
     * @param pos position of spot
     * @return colour requirement or null if no spot or no requirement
     */
    public Colour spotRequiredHeadColour(Position pos) {
        Spot spot = spotMatrix[pos.getX()][pos.getY()];
        if (spot == null) {
            return null;
        }
        return spot.getRequiredHeadColour();
    }

    /**
     * @param pos position of spot
     * @return true if position is a spot
     */
    boolean isSpot(Position pos) {
        Spot spot = spotMatrix[pos.getX()][pos.getY()];
        return spot != null;
    }

    /**
     * Visualise the static (non-caterpillar) parts of the board
     *
     * @return a string representation of the board
     */
    public String visualiseStatic() {
        StringBuilder staticBoard = new StringBuilder();
        String padding = " ";
        // Header for labelling columns
        staticBoard.append("x ");
        for (int x = 0; x < Board.BOARD_WIDTH; x++) {
            staticBoard.append(padding);
            staticBoard.append(x);
        }
        staticBoard.append(System.lineSeparator());
        staticBoard.append("y");
        staticBoard.append(System.lineSeparator());
        // Each row of apple
        for (int y = 0; y < Board.BOARD_WIDTH; y++) {
            // Adding labels for rows
            staticBoard.append(y);
            staticBoard.append(" ");
            for (int x = 0; x < Board.BOARD_WIDTH; x++) {
                Spot spot = spotMatrix[x][y];
                staticBoard.append(padding);
                if (spot == null) {
                    staticBoard.append(" ");
                    continue;
                }
                Colour colour = spot.getRequiredHeadColour();
                if (colour == null) {
                    staticBoard.append("O");
                    continue;
                }
                String mark = switch (colour) {
                    case GREEN -> "G";
                    case TURQUOISE -> "T";
                    case YELLOW -> "Y";
                    case WILDCARD -> "W";
                };
                staticBoard.append(mark);
            }
            staticBoard.append(System.lineSeparator());
        }

        return staticBoard.toString();
    }
}
