package WizardTD;

import WizardTD.subtiles.*;
import processing.core.PApplet;
import java.util.List;
import java.util.ArrayList;
import processing.core.PImage;

public class Board {
    private static Tile[][] tiles = new Tile[20][20]; // 20x20 board

    public void loadLayout(String filename, App app) {  // Change PApplet to App
        String[] lines = app.loadStrings(filename);
        
        for (int i = 0; i < 20; i++) { // always 20 rows
            for (int j = 0; j < 20; j++) { // always 20 columns
                char c = ' '; // default to GrassTile
                if (i < lines.length && j < lines[i].length()) { // if within bounds of the line
                    c = lines[i].charAt(j);
                }    
                switch (c) {
                    case 'S':
                        tiles[i][j] = new ShrubTile(app);
                        break;
                    case 'X':
                        tiles[i][j] = new PathTile(j, i, app, this);
                        break;
                    case 'W':
                        tiles[i][j] = new WizardHouseTile(app, j, i, this); 
                        break;
                    default:
                        tiles[i][j] = new GrassTile(app);
                        break;
                }
            }
        }

        // Now, after all tiles are initialized, decide the image for PathTiles
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] instanceof PathTile) {
                    ((PathTile) tiles[i][j]).decidePathImage(app);
                }
            }
        }
    }

    public boolean isPathTile(int x, int y) {
        if (x < 0 || x >= tiles[0].length || y < 0 || y >= tiles.length) {
            return true;  // Treat the boundary as X
        }
        Tile tile = tiles[y][x];
        return tile instanceof PathTile || tile instanceof WizardHouseTile;
    }

    public void render(PApplet app) {
        app.noTint();
    
        // First, render all tiles except tower tiles
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (!(tiles[i][j] instanceof TowerTile)) {
                    tiles[i][j].render(j * App.CELLSIZE, i * App.CELLSIZE + App.TOPBAR, app);
                }
            }
        }
    
        // Then, render all tower tiles
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] instanceof TowerTile) {
                    tiles[i][j].render(j * App.CELLSIZE, i * App.CELLSIZE + App.TOPBAR, app);
                }
            }
        }
    }
    
        
    public void renderWizardHouse(PApplet app) {
        GrassTile grass = new GrassTile(app); // Create an instance of GrassTile

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] instanceof WizardHouseTile) {
                    // First, render the GrassTile at the same position
                    grass.render(j * App.CELLSIZE, i * App.CELLSIZE + App.TOPBAR, app);

                    // Then, render the WizardHouseTile on top of it
                    tiles[i][j].render(j * App.CELLSIZE, i * App.CELLSIZE + App.TOPBAR, app);
                }
            }
        }
    }

    public boolean isWizardHouseTile(int x, int y) {
        if (x < 0 || x >= tiles[0].length || y < 0 || y >= tiles.length) {
            return false;
        }
        return tiles[y][x] instanceof WizardHouseTile;
    }
    

    public TowerTile placeTower(int x, int y, PApplet app, float towerRange, float towerFiringSpeed, float towerDamage) {
        if (x >= 0 && x < tiles[0].length && y >= 0 && y < tiles.length) {
            Tile tile = tiles[y][x];
            
            // Check if it's a GrassTile
            if (tile instanceof GrassTile) {
                // Replace with a TowerTile and pass the coordinates
                TowerTile tower = new TowerTile(app, x, y, towerRange, towerFiringSpeed, towerDamage);
                tiles[y][x] = tower;
                return tower;  // Successfully placed the tower and return it
            }
        }
        return null;  // Failed to place the tower
    }

    public void notifyFireballsToDespawn(Monster monster) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] instanceof TowerTile) {
                    ((TowerTile) tiles[i][j]).despawnFireballsTargeting(monster);
                }
            }
        }
    }
    
    
    public void upgradeTowerRange(int mouseX, int mouseY, PApplet app) {
        int tileX = mouseX / App.CELLSIZE;
        int tileY = (mouseY - App.TOPBAR) / App.CELLSIZE;
        System.out.println("Trying to upgrade range at tile: (" + tileX + ", " + tileY + ")");  // Debugging line
        
        if (tileX >= 0 && tileX < tiles[0].length && tileY >= 0 && tileY < tiles.length) {
            Tile tile = tiles[tileY][tileX];
            if (tile instanceof TowerTile) {
                System.out.println("Upgrading a TowerTile for Range");  // Debugging line
                ((TowerTile) tile).upgradeRange();
            }
            else {
                System.out.println("Clicked tile is not a TowerTile");  // Debugging line
            }
        }
    }

    public void upgradeTowerSpeed(int mouseX, int mouseY, PApplet app) {
        int tileX = mouseX / App.CELLSIZE;
        int tileY = (mouseY - App.TOPBAR) / App.CELLSIZE;
        System.out.println("Trying to upgrade speed at tile: (" + tileX + ", " + tileY + ")");  // Debugging line
        
        if (tileX >= 0 && tileX < tiles[0].length && tileY >= 0 && tileY < tiles.length) {
            Tile tile = tiles[tileY][tileX];
            if (tile instanceof TowerTile) {
                System.out.println("Upgrading a TowerTile for speed");  // Debugging line
                ((TowerTile) tile).upgradeSpeed();
            }
            else {
                System.out.println("Clicked tile is not a TowerTile");  // Debugging line
            }
        }
    }
    
    public void upgradeTowerDamage(int mouseX, int mouseY, PApplet app) {
        int tileX = mouseX / App.CELLSIZE;
        int tileY = (mouseY - App.TOPBAR) / App.CELLSIZE;
        System.out.println("Trying to upgrade damage at tile: (" + tileX + ", " + tileY + ")");  // Debugging line
        
        if (tileX >= 0 && tileX < tiles[0].length && tileY >= 0 && tileY < tiles.length) {
            Tile tile = tiles[tileY][tileX];
            if (tile instanceof TowerTile) {
                System.out.println("Upgrading a TowerTile for damage");  // Debugging line
                ((TowerTile) tile).upgradeDamage();
            }
            else {
                System.out.println("Clicked tile is not a TowerTile");  // Debugging line
            }
        }
    } 
    

    public Tile[][] getTiles() {
        return tiles;
    }
    
    
}