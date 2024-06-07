package GAME;

//description of class
// The Players class is a class used to represent players in a game.
// Each player has can play settler and village pieces on the board. Throughout the game players collect resources,
// and explore and occupying new islands.
// In the Player class, each player has a name, colour and score. Other fields keep track of player piece count and resources collected.
// A player wins the game if they have the most points at the end of the game.

import java.util.ArrayList;
import java.util.List;

public class Player {

    public int playerID = 0;
    public int playerScore = 0;
    public int playerCoconut = 0;
    public int playerBamboo = 0;
    public int playerWater = 0;
    public int playerPreciousStone = 0;
    public int playerStatuette = 0;
    public int[][] settlerCoords = {}; // player settler positions on Board (array of x, y coordinates)
    public int[][] villageCoords = {}; // player village positions on Board (array of x, y coordinates)

    public Player(int playerID, int playerScore, int playerCoconut, int playerBamboo, int playerWater, int[][] settlerCoords, int[][] villageCoords) {
        this.playerID = playerID;
        this.playerScore = playerScore;
        this.playerCoconut = playerCoconut;
        this.playerBamboo = playerBamboo;
        this.playerWater = playerWater;
        this.settlerCoords = settlerCoords;
        this.villageCoords = villageCoords;
    }

    public Player(int playerID, int playerScore, int playerCoconut, int playerBamboo, int playerWater, int playerPreciousStone, int playerStatuette, int[][] settlerCoords, int[][] villageCoords) {
        this.playerID = playerID;
        this.playerScore = playerScore;
        this.playerCoconut = playerCoconut;
        this.playerBamboo = playerBamboo;
        this.playerWater = playerWater;
        this.playerPreciousStone = playerPreciousStone;
        this.playerStatuette = playerStatuette;
        this.settlerCoords = settlerCoords;
        this.villageCoords = villageCoords;
    }


    String name;
    String colour;
    int resources; //number of resources collected (for each resource)
    int statuettes; // number of statuettes collected
    int score = 0; //player score
    int countVillages; // count of remaining village tokens to place
    int countSettlers; //count of remaining settler tokens to place
    int turnOrder; // order that players make moves

    public static String getName() {
        return "";
    } // gets player name

    public static String getColour() {
        return "";
    } // gets player colour

    public static int getResourcesCount() {
        return 0;
    } //gets player resources count, taking resource as argument

    public static int getScore() {
       return 0;
    } // gets player score

    public static int numberofPlayers() {
        return 0;
    } // check how many players in the game, given game set up is dependent on number of players.

    public List<Position> getOccupiedPosList() {
        List<Position> list = new ArrayList<>();
        for (int i = 0; i < settlerCoords.length; i++) {
            for (int j = 0; j < settlerCoords[0].length; j++) {
                if (settlerCoords[i][j] == 1) {
                    list.add(Position.positions[i][j]);
                }
            }
        }
        for (int i = 0; i < villageCoords.length; i++) {
            for (int j = 0; j < villageCoords[0].length; j++) {
                if (villageCoords[i][j] == 1) {
                    list.add(Position.positions[i][j]);
                }
            }
        }
        return list;
    }
}
