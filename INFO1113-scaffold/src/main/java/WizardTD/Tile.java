package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

public abstract class Tile {
    protected PImage image; // This will hold the image for the tile, loaded using Processing's loadImage method.

    // Every tile will have a render method to draw itself on the board.
    public abstract void render(int x, int y, PApplet app);
}



