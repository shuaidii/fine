package GAME;

//description of class
//The settler class represents a settler or explorer that the player uses to occupy island territory.
// Each player starts the game with a certain number of settlers // who they can place on the board (rules depend on game phase).
// The Settlers class contains information about the number of settlers a player has and their position on the board.
public class Settlers {

    String colour; // player colour of token
    int [][] position = {}; // board position of token
    String type; //boat or land

    public static String getColour() {
        return "";
    } // get colour of settler token

    public static boolean isPlaced() {
        return false;
    } // check if settler is on the board

    public static void setNumberofSettlers() {

    } // sets number of settlers in the game (varies depending on number of players)

    public static void removeAllSettlers() {

    } // remove all settlers from the board at end of exploration phase

}
