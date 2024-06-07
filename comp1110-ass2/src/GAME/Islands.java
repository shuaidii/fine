package GAME;

//description of class
//The Islands class represents the various islands that make up the game board.
// There are several types of islands, each with its own unique features.
// Players can claim territories on any island they choose, but they must be careful
// not to spread their settlers too thin. The Islands class contains information
// about the point value and position of each island on the Board. The class includes methods
// for checking placement of pieces relating to islands.

import comp1110.ass2.BlueLagoon;

import java.util.Arrays;

public class Islands {
    public int pointValue; //island point value [6, 8 or 10]
    public int[][] islandCoords = {}; // island position on Board (array of x, y coordinates)

    public Islands(int pointValue, int[][] islandCoords) {
        this.pointValue = pointValue;
        this.islandCoords = islandCoords;
    }

    @Override
    public String toString() {
        return "pointValue: " + this.pointValue + ", islandCoords: " + Arrays.toString(this.islandCoords);
    }

    public static void setPointValue() {

    } // sets the point value of each island

    public static boolean isOnIsland() {
        return false;
    } // determine whether player has placed any tokens on an island

    public static int countPlayerIslands() {
        return 0;
    } // count number of islands that player has placed tokens on

    public static int countisLinked() {
        return 0;
    } // check whether islands with a player’s tokens are linked and count the longest chain of linked islands

    public static String mostTokens() {
        return "";
    } // count number of tokens that each player has placed on an island, return player with most tokens

}
