package WizardTD.subtiles;
import WizardTD.Tile;
import WizardTD.Board;
import WizardTD.App;

import processing.core.PApplet;
import processing.core.PImage;

public class PathTile extends Tile {
    private int x, y; // tile position on the board
    private Board board;
    private PImage image;
    protected boolean visited = false;

    public PathTile(int x, int y, PApplet app, Board board) {
        this.x = x;
        this.y = y;
        this.board = board;
    }

    public void decidePathImage(PApplet app) {
        // Set the default image first
        image = app.loadImage("src/main/resources/WizardTD/beetle.png");
        boolean leftIsPath = board.isPathTile(x - 1, y);
        boolean rightIsPath = board.isPathTile(x + 1, y);
        boolean topIsPath = board.isPathTile(x, y - 1);
        boolean bottomIsPath = board.isPathTile(x, y + 1);
        
        int adjacentPaths = 0;
        if (leftIsPath) adjacentPaths++;
        if (rightIsPath) adjacentPaths++;
        if (topIsPath) adjacentPaths++;
        if (bottomIsPath) adjacentPaths++;

        if (adjacentPaths == 1) {
            if (leftIsPath || rightIsPath) {
                image = app.loadImage("src/main/resources/WizardTD/path0.png");
            } else {
                image = app.loadImage("src/main/resources/WizardTD/path0.png");
                image = ((App) app).rotateImageByDegrees(image, 90);
            }
            return;
        }
        // Cross Junction
        if (leftIsPath && rightIsPath && topIsPath && bottomIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path3.png");
        } 
        // T-Junctions
        else if (leftIsPath && rightIsPath && topIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path2.png");
            image = ((App) app).rotateImageByDegrees(image, 180);
        } else if (leftIsPath && rightIsPath && bottomIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path2.png");
        } else if (topIsPath && bottomIsPath && leftIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path2.png");
            image = ((App) app).rotateImageByDegrees(image, 90);
        } else if (topIsPath && bottomIsPath && rightIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path2.png");
            image = ((App) app).rotateImageByDegrees(image, -90);
        } 
        // Corners
        else if (topIsPath && leftIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path1.png");
            image = ((App) app).rotateImageByDegrees(image, 90);
        } else if (topIsPath && rightIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path1.png");
            image = ((App) app).rotateImageByDegrees(image, 180);
        } else if (bottomIsPath && leftIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path1.png");
        } else if (bottomIsPath && rightIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path1.png");
            image = ((App) app).rotateImageByDegrees(image, -90);
        } 
        // Straight paths
        else if (leftIsPath && rightIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path0.png");
        } else if (topIsPath && bottomIsPath) {
            image = app.loadImage("src/main/resources/WizardTD/path0.png");
            image = ((App) app).rotateImageByDegrees(image, 90);
        }
    }

    @Override
    public void render(int x, int y, PApplet app) {
        app.image(image, x, y);
    }
}


