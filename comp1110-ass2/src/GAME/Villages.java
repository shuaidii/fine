package GAME;

//description of class
//The village class represents the village pieces that players can place on the board.
// Players can build villages on any territory they and the settlers claim.
// The Village class contains information about the number of villages owned
// by each player and their locations, and methods to check if place in stone circle and remove from board.

public class Villages {
    String colour; //village colour,
    int [][] position = {};

    public static String getColour() {
        return "";
    } //get village colour

    public static boolean isVillagePlaced() {
        return false;
    } // check if village is on the board

    public static boolean isPlacedStoneCircle() {
        return false;
    } // check if village is placed on a stone circle

    public static void removeVillages() {

    } // villages placed on a stone circle are removed from the board at the beginning of the settlement phase

}
