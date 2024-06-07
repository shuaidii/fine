package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.List;
import java.util.ArrayList;
import WizardTD.subtiles.*;
import WizardTD.PathFinder.Node;


public class Monster {

    protected PImage image;
    protected App game;
    protected float x, y, targetX, targetY; 
    protected Board board;
    protected String direction = "";  // Can be "up", "down", "left", "right"
    List<String> pathToWizard;
    int pathIndex = 0;
    protected PathFinder pathFinder = new PathFinder();
    protected float speed;  // speed of the monster in pixels per frame
    protected float leftoverMove = 0.0f;  
    protected int spawnDelay; 
    protected static final float EPSILON = 0.05f; 
    protected List<Node> currentPath = null;



    protected float hp;  // Max hit points (initial health)
    protected float currentHp;  // Current health
    protected float armour;  // Percentage multiplier to damage received
    protected float manaGainedOnKill;  // Mana gained when this monster is killed
    protected String type;  // The sprite image to use for the monster
    protected PImage deathImages[];  // Images for death animation
    protected int deathFrameCount = 0;  // Frame counter for death animation
    protected float manaMultiplier = 1.0f; 


    public Monster(Board board, PApplet app, float speed, int spawnDelay, float hp, float armour, float manaGainedOnKill, String type, App game) {
        this.board = board;
        this.game = game;
        this.image = app.loadImage("src/main/resources/WizardTD/" + type + ".png");
        this.hp = hp;
        this.currentHp = hp;
        this.armour = armour;
        this.manaGainedOnKill = manaGainedOnKill;
        this.type = type;
        this.speed = speed;
        this.spawnDelay = spawnDelay;
        List<int[]> spawnPoints = getSpawnPoints();
        int[] chosenSpawn = spawnPoints.get((int) (Math.random() * spawnPoints.size()));
        this.x = (float) chosenSpawn[0];
        this.y = (float) chosenSpawn[1];
        this.speed = speed;
        this.spawnDelay = spawnDelay; 

        List<PImage> deathImageList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String imagePath = "src/main/resources/WizardTD/" + type + (i+2) + ".png";
            PImage deathImage = safeLoadImage(app, imagePath);
            if (deathImage != null) {  // Check if the image was loaded successfully
                deathImageList.add(deathImage);
            }
        }
        // Convert the list to an array for consistency
        deathImages = deathImageList.toArray(new PImage[0]);
    }

    protected PImage safeLoadImage(PApplet app, String path) {
    try {
        return app.loadImage(path);
    } catch (Exception e) {
        // Image not found or other error, return null
        return null;
    }
    }

    protected List<int[]> getSpawnPoints() {
        List<int[]> spawnPoints = new ArrayList<>();
        Tile[][] tiles = board.getTiles();
    
        // Check the top row
        for (int j = 0; j < tiles[0].length; j++) {
            if (tiles[0][j] instanceof PathTile) {
                spawnPoints.add(new int[]{j, -1});  // Spawn above the board
            }
        }
    
        // Check the bottom row
        for (int j = 0; j < tiles[tiles.length - 1].length; j++) {
            if (tiles[tiles.length - 1][j] instanceof PathTile) {
                spawnPoints.add(new int[]{j, tiles.length});  // Spawn below the board
            }
        }
    
        // Check the leftmost column
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i][0] instanceof PathTile) {
                spawnPoints.add(new int[]{-1, i});  // Spawn to the left of the board
            }
        }
    
        // Check the rightmost column
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i][tiles[i].length - 1] instanceof PathTile) {
                spawnPoints.add(new int[]{tiles[i].length, i});  // Spawn to the right of the board
            }
        }
        return spawnPoints;
    }

    public void moveWithSpeed() {
        if (spawnDelay > 0) {
            // If there's a spawn delay, decrement it and do not move the monster
            spawnDelay--;
            return;
        }
        float totalMove = speed + leftoverMove;
        int intMove = (int) totalMove;  // integer part of the movement
        leftoverMove = totalMove - intMove;  // store the fractional part for next frame
        
        for (int i = 0; i < intMove; i++) {
            move();  // call the existing move method
        }
    }
    
    public void move() {
        Tile[][] tiles = board.getTiles();
        
        if (x < 0 || x >= tiles[0].length || y < 0 || y >= tiles.length) {
            moveOutside(tiles);
        } else {
            moveInside(tiles);
        }
    }
    
    protected void moveOutside(Tile[][] tiles) {
        if (y == -1) direction = "down";
        else if (y == tiles.length) direction = "up";
        else if (x == -1) direction = "right";
        else if (x == tiles[0].length) direction = "left";
        
        moveInDirection();
    }
    
    protected void moveInDirection() {
        switch (direction) {
            case "up":
                y -= 1.0f / 32;
                break;
            case "down":
                y += 1.0f / 32;
                break;
            case "left":
                x -= 1.0f / 32;
                break;
            case "right":
                x += 1.0f / 32;
                break;
        }
    }

    protected void resetMonsterPosition() {
        List<int[]> spawnPoints = getSpawnPoints();
        int[] chosenSpawn = spawnPoints.get((int) (Math.random() * spawnPoints.size()));
        this.x = (float) chosenSpawn[0];
        this.y = (float) chosenSpawn[1];
        
        // Reset target coordinates
        this.targetX = 0;
        this.targetY = 0;
    } 
    protected boolean isNearTarget() {
    return Math.abs(x - targetX) < EPSILON && Math.abs(y - targetY) < EPSILON;
    }
    protected boolean isAlignedWithTile() {
        return Math.abs(x - Math.round(x)) < EPSILON && Math.abs(y - Math.round(y)) < EPSILON;
    }

    protected Node findWizardHouseTileGoal(Tile[][] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j] instanceof WizardHouseTile) {
                    return pathFinder.new Node(j, i);
                }
            }
        }
        return null;
    }
     /**
     * moveInside method
     *          
     * This method implements the A* pathfinding algorithm,
     *
     * For details on the A* algorithm, refer to the credits in the PathFinder class.
     */
     protected void moveInside(Tile[][] tiles) {
        if (isNearTarget() || targetX == 0 && targetY == 0) {
            if (currentPath == null || currentPath.isEmpty()) {
                Node start = pathFinder.new Node((int) x, (int) y);
                Node goal = findWizardHouseTileGoal(tiles);
                
                currentPath = pathFinder.findPath(start, goal, tiles);
                if (currentPath != null && currentPath.size() > 0) {
                    currentPath.remove(0);  // Remove the starting node, as the monster is already there
                }
            }
            
            if (currentPath != null && !currentPath.isEmpty()) {
                Node nextTile = currentPath.get(0);
                currentPath.remove(0);  // Monster is moving to this tile, so remove it from the path
                targetX = nextTile.x;
                targetY = nextTile.y;
            } else {
                targetX = x;
                targetY = y;
            }
        }
    
        // Move towards the target in pixel increments
        float deltaX = targetX - x;
        float deltaY = targetY - y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 0) {
            float moveX = (speed / 32.0f) * (deltaX / distance);
            float moveY = (speed / 32.0f) * (deltaY / distance);
            
            // Check if the move would overshoot the target
            float new_distance_after_move = (float) Math.sqrt((deltaX - moveX) * (deltaX - moveX) + (deltaY - moveY) * (deltaY - moveY));
            if (new_distance_after_move > distance) {
                // If it would overshoot, just move to the target
                x = targetX;
                y = targetY;
                leftoverMove += (new_distance_after_move - distance) * 32.0f / speed;  // Store the leftover movement for next frame
            } else {
                x += moveX;
                y += moveY;
            }
        }
     }


     public float getDirectionAngle() {
        switch (direction) {
            case "up": return 270;
            case "down": return 90;
            case "left": return 180;
            case "right": return 0;
            default: return 0;
        }
    }

     public void reduceHealth(float damage) {
        float actualDamage = damage * armour;  // Calculate actual damage after considering armour
        currentHp -= actualDamage;  // Reduce the monster's health by the actual damage
        if (currentHp <= 0 && deathFrameCount == 0) {  // Ensure the death sequence is only initiated once
            onDeath();
        }
    }
    
    public void onDeath() {
        // Start the death animation
        deathFrameCount = 1;
        speed = 0;  // Stop the monster's movement
        game.addMana(manaGainedOnKill * manaMultiplier);  // Updated to consider multiplier
    }

    public void update() {
        if (deathFrameCount > 0 && deathFrameCount < 20) {
            deathFrameCount++;
        }
        if (board.isWizardHouseTile((int) this.x, (int) this.y)) {
            // Monster has reached the WizardHouseTile
            game.deductMana((int) this.currentHp);  // Decrement wizard's mana by monster's current HP
            board.notifyFireballsToDespawn(this); 
            resetMonsterPosition();  // Reset monster's position to a spawn point
        }        
    }

    public float getCurrentHp() {
        return this.currentHp;
    }

    public void setManaMultiplier(float multiplier) {
        this.manaMultiplier = multiplier;
    }


    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    

    public float getSpeed() {
        return this.speed;
    }

    public PImage getImage() {
        return this.image;
    }
    
    public float getArmour() {
        return armour;
    }

    public float getManaGainedOnKill() {
        return manaGainedOnKill;
    }

    public String getType() {
        return type;
    }

    
    public void render(PApplet app) {
        app.noTint();
        if (x != -1.0f && y != -1.0f) { // Only render if the monster hasn't disappeared
            int drawX = (int) (x * App.CELLSIZE + App.CELLSIZE / 2 - image.width / 2);
            int drawY = (int) (y * App.CELLSIZE + App.CELLSIZE / 2 - image.height / 2 + App.TOPBAR);

            // Display HP bar
            float hpPercentage = PApplet.constrain(currentHp / hp, 0, 1);  // Clamp between 0 and 1
            app.fill(255, 0, 0);  // Red color for missing HP
            app.rect(drawX, drawY - 10, image.width, 5);
            app.fill(0, 255, 0);  // Green color for current HP
            app.rect(drawX, drawY - 10, image.width * hpPercentage, 5);

            if (deathFrameCount == 0) {
                // If not in death animation, display the normal monster image
                app.image(image, drawX, drawY);
            } else if (deathFrameCount < deathImages.length * 4) {  
                // If in death animation, display the death images based on the frame count
                int deathImageIndex = (deathFrameCount / 4) % deathImages.length;
                app.image(deathImages[deathImageIndex], drawX, drawY);
            } else {
                // After the death animation is over, remove the monster from the game
                x = -1.0f;
                y = -1.0f;
            }
        }      
    }

    
}