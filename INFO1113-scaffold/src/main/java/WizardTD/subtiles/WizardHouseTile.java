package WizardTD.subtiles;
import WizardTD.Tile;
import processing.core.PApplet;
import processing.core.PImage;
import WizardTD.App;
import WizardTD.Board;

public class WizardHouseTile extends Tile {
    private PImage image;
    private Board board;
    private int x, y;  // Add fields to store x and y coordinates

    public WizardHouseTile(App app, int x, int y, Board board) {
        this.image = app.loadImage("src/main/resources/WizardTD/wizard_house.png");
        this.board = board;
        this.x = x;  // Initialize x coordinate
        this.y = y;  // Initialize y coordinate
    }

    @Override
    public void render(int x, int y, PApplet app) {
        PImage rotatedImage = image;
    
        boolean topIsPath = board.isPathTile(this.x, this.y - 1);
        boolean bottomIsPath = board.isPathTile(this.x, this.y + 1);
        boolean leftIsPath = board.isPathTile(this.x - 1, this.y);
    
        if (topIsPath) {
            rotatedImage = ((App) app).rotateImageByDegrees(image, 270);
        }
        else if (bottomIsPath){
            rotatedImage = ((App) app).rotateImageByDegrees(image, 90);
        }
        else if (leftIsPath){
            rotatedImage = ((App) app).rotateImageByDegrees(image, 180);
        }
        else {
            rotatedImage = ((App) app).rotateImageByDegrees(image, 0);
        }
    
        // Adjust the position to center the WizardHouseTile
        app.image(rotatedImage, x - 8, y - 8);
    }
    
}
