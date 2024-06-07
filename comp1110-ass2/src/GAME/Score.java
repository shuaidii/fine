package GAME;

//description of class
// The Score class represents the game scoreboard in Blue Lagoon.
// It contains a player id to score mapping.
// The Score class also includes methods for increasing a player's score depending on certain conditions,
// obtaining a player's score, and checking whether a player has won the game.

public class Score {
    int score; // count of score

    public static int calculateScore() {
        return 0;
    } // increases players score at the end of phase

    public static int calculateTotalIslandsScore() {
        return 0;
    } // add score for islands

    public static int numberofIslands() {
        return 0;
    } // add score for number of islands player has tokens on (points for 7 or 8)

    public static int numberLinkedIslands() {
        return 0;
    } // add score for longest chain of linked islands a player has tokens on

    public static int calculateIslandMajoritiesScore() {
        return 0;
    } // add score for islands that player has majority on

    public static int calculateResourcesandStatuettesScore() {
        return 0;
    } // add score for resources and statuettes

    public static int numberMatchingResources() {
        return 0;
    } //add score for number of resources of the same type each player has

    public static int hasEachResource() {
        return 0;
    } // add score if player has one of each resource

    public static int numberStatuettes() {
        return 0;
    } // add score for each statuette a player has

    public static void determineResults() {

    } // compares player scores and returns player rank and player score in order. In the case of a tie the player with the most resources and statuettes wins, otherwise tie remains.

}