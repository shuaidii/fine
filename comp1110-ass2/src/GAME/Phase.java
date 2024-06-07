package GAME;

//description of class
// The Phase class represents the current phase of the game.
// The game is divided into two phases: exploration and settlement.
// In the exploration phase, players can only place their settler
// on the sea or on an unclaimed linked space on land, while in the settlement phase, players can
// place pieces next to an unoccupied space positioned next a space they are already occupying.
// The Phase class contains information about the current phase of the game and conditions to test whether phase ends.

public class Phase {
    String phase = "Exploration"; //exploration or settlement)


    public static boolean isPhaseOver() {
        return false;
    } // check if phase over

    public static boolean allResourcesCollected() {
        return false;
    } // check whether all resources (coconut, bamboo, water, precious stones) on the board have been collected (not including statuettes)

    public static boolean allSettlersVillagesPlaced() {
        return false;
    } // check whether all settlers and villages have been placed on the board for settlement phase:

    public static boolean allSettlersPlaced() {
        return false;
    } //check whether all settlers and have been placed on the board

    public static void getPlayerEndPhase() {

    } //gets the player that ended the exploration phase
}
