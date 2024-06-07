package WizardTD.subtiles;
import WizardTD.Tile;
import processing.core.PApplet;
import processing.core.PImage;

public class ShrubTile extends Tile {
    private PImage image;

    public ShrubTile(PApplet app) {
        image = app.loadImage("src/main/resources/WizardTD/shrub.png");
    }

    @Override
    public void render(int x, int y, PApplet app) {
        app.image(image, x, y);
    }
}
