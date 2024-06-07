package comp1110.ass1.board;

import comp1110.ass1.caterpillar.Caterpillar;
import comp1110.ass1.caterpillar.Colour;

/**
 * A location on the apple puzzle board that a caterpillar can occupy.
 *
 * The Spot tracks its index-based location on the puzzle board, any occupying
 * caterpillar segments, and any requirements for caterpillar heads.
 */
public class Spot {
    // Caterpillar whose segment occupies spot if any
    private Caterpillar occupier = null;

    // Colour of caterpillar whose head is required to occupy spot, can be
    // null for no requirement
    private Colour requiredHeadColour = null;

    /**
     * Occupy spot with caterpillar
     *
     * @param caterpillar caterpillar to occupy spot with
     */
    public void occupy(Caterpillar caterpillar) {
        occupier = caterpillar;
    }

    /**
     * @return true if spot is occupied with segment
     */
    public boolean isOccupied() {
        return occupier != null;
    }

    /**
     * @return occupying caterpillar, else null
     */
    public Caterpillar getOccupier() {
        return occupier;
    }

    /**
     * Clear / remove any caterpillars occupying spot
     */
    public void unoccupy() {
        occupier = null;
    }

    /**
     * @return colour of caterpillar required to occupy spot with head, else null if none
     */
    public Colour getRequiredHeadColour() {
        return this.requiredHeadColour;
    }

    /**
     * Set colour of caterpillar required to occupy spot with head
     */
    public void setRequiredHeadColour(Colour colour) {
        this.requiredHeadColour = colour;
    }
}
