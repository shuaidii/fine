package WizardTD.subtiles;
import WizardTD.Tile;


import processing.core.PApplet;
import processing.core.PImage;

public class GrassTile extends Tile {
    private PImage image;

    public GrassTile(PApplet app) {
        image = app.loadImage("src/main/resources/WizardTD/grass.png");
    }

    @Override
    public void render(int x, int y, PApplet app) {
        app.image(image, x, y);
    }
}






