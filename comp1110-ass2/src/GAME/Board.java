package GAME;

//description of class
//Represents the game board in the Blue Lagoon game. This class includes
// a two-dimensional array of island objects to represent the various island
// on the game board and their locations, and stone circle locations. The Board class also includes methods
// for managing game progress, such as placing villages and Settlers, moving Settlers
// collecting resources, and calculating scores.
public class Board {

    int [][] Board = {}; //Create new Board object [x][y];

    public static void addIslands() {

    } // add islands to board

    public static void addStoneCircles() {

    } // add stone circles to board

    public static boolean isOccupied() {
        return false;
    } // check whether a board position is occupied

    public static String getPhase() {
        return "";
    } // get the current play phase (exploration or settlement)

    public static boolean isMoveValid() {
        return false;
    } // check whether move is valid

    public static int generateAllValidMoves() {
        return 0;
    } // returns a list of all valid moves that can be played by a given player

    public static boolean canPlaceSettlerExploration() {
        return false;
    } // for exploration phase: check whether the space is not an island and is not occupied (place token boat side up) OR check whether the space is an island and positioned next to a space already occupied by one of player’s pieces (place token land side up). Places settler.

    public static boolean canPlaceSettlerSettlement() {
        return false;
    } // for settlement phase: check whether the space is positioned next to a space already occupied by one of player’s pieces

    public static boolean canPlaceVillage() {
        return false;
    } // check whether the space is an island, is not occupied and positioned next to a space already occupied by one of player’s pieces.

    public static void placePiece() {

    } // places piece on board (settler (token land side up for land and boat side up for sea) or village)

    public static void startSettlementPhase() {

    } // resets the board for the settlement phase

}









