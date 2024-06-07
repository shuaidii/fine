package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;
import WizardTD.subtiles.TowerTile;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    public Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.
     // The board instance.
    private Board board;
    private TopBar topBar;
    private Sidebar sidebar;
    public int initialMana;
    public int initialManaCap;
    public int manaGainedPerSecond;
    public int mana;
    private float manaUpdateTimer = 0.0f;

    public float manaPoolSpellInitialCost;
    public float manaPoolSpellCostIncreasePerUse;
    public float manaPoolSpellCapMultiplier;
    public float manaPoolSpellManaGainedMultiplier;
    public float currentManaPoolSpellCost;  // To keep track of the current cost after multiple uses
    private float manaMultiplier = 1.0f;  // New attribute to store the mana multiplier



    private float totalGameTime = 0.0f;  // Total game elapsed time in seconds
    private long lastFPressTime = 0;



    //Monsters
    Monster monster;
    private List<Monster> activeMonsters = new ArrayList<>();
    private List<Wave> waves = new ArrayList<>();  // List to manage waves
    private int currentWaveIndex = 0;  // Track the current wave
    private float waveTimer = 0;

    //towers
    private boolean towerPlacementMode = false;
    private boolean gamePaused = false;

    public float initialTowerRange;
    public float initialTowerFiringSpeed;
    public float initialTowerDamage;
    public int towerBaseCost;
    private TowerTile selectedTower;

    // New attributes for multilevel
    private JSONArray levels;
    private int currentLevelIndex;
    private JSONObject currentLevelConfig;
    private JSONObject config;

    // New level transition
    private boolean isTransitioning = false;
    private float transitionTimer = 0.0f;
    private float transitionDuration = 2.0f; // 2 seconds transition



    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        config = loadJSONObject(configPath);
    
        // Load root-level configuration values
        initialTowerRange = config.getInt("initial_tower_range");
        initialTowerFiringSpeed = config.getFloat("initial_tower_firing_speed");
        initialTowerDamage = config.getInt("initial_tower_damage");
        initialMana = config.getInt("initial_mana");
        mana = initialMana; // Initialize the mana to the initial value
        initialManaCap = config.getInt("initial_mana_cap");
        manaGainedPerSecond = config.getInt("initial_mana_gained_per_second");
        towerBaseCost = config.getInt("tower_cost");
        manaPoolSpellInitialCost = config.getFloat("mana_pool_spell_initial_cost");
        manaPoolSpellCostIncreasePerUse = config.getFloat("mana_pool_spell_cost_increase_per_use");
        manaPoolSpellCapMultiplier = config.getFloat("mana_pool_spell_cap_multiplier");
        manaPoolSpellManaGainedMultiplier = config.getFloat("mana_pool_spell_mana_gained_multiplier");
        currentManaPoolSpellCost = manaPoolSpellInitialCost;
    
        if (config.hasKey("levels")) {
            levels = config.getJSONArray("levels");
            currentLevelIndex = 0;
            currentLevelConfig = levels.getJSONObject(currentLevelIndex);
        } else {
            currentLevelConfig = config;
        }
    
        initializeLevel();
    }
    
    
    private void initializeLevel() {
        // Only fetch level-specific keys here
        board = new Board(); // initialize the board
        board.loadLayout(currentLevelConfig.getString("layout"), this);  
        topBar = new TopBar(WIDTH, TOPBAR, mana, initialManaCap);
        sidebar = new Sidebar(SIDEBAR, HEIGHT, this);
    
        JSONArray wavesConfig = currentLevelConfig.getJSONArray("waves");
        System.out.println("Initializing level with wave count: " + wavesConfig.size());

        for (int i = 0; i < wavesConfig.size(); i++) {
            JSONObject waveConfig = wavesConfig.getJSONObject(i);
            int duration = waveConfig.getInt("duration");
            float preWavePause = waveConfig.getFloat("pre_wave_pause");
            JSONArray monstersConfig = waveConfig.getJSONArray("monsters");
    
            for (int j = 0; j < monstersConfig.size(); j++) {
                JSONObject monsterConfig = monstersConfig.getJSONObject(j);
                Wave wave = new Wave(duration, preWavePause, monsterConfig, board, this);
                waves.add(wave);
            }
        }        
    }

    public void resetGame() {
        // Reset the game variables to their initial state
        mana = initialMana;
        manaMultiplier = 1.0f;
        totalGameTime = 0.0f;
        lastFPressTime = 0;
        currentWaveIndex = 0;
        waveTimer = 0.0f;
        activeMonsters.clear();
        waves.clear();
        towerPlacementMode = false;
        gamePaused = false;
        
        // Reload the initial setup
        setup();
        }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed() {
        if (key == 'F' || key == 'f') {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFPressTime < 300) {  // Double press within 300ms
                sidebar.toggleSpeedMode();
            }
            lastFPressTime = currentTime;
        }

        if (key == 'P' || key == 'p') {
            sidebar.togglePauseMode();
            gamePaused = sidebar.isPauseActive();
        }

        if (key == 'R' || key == 'r') {
            resetGame();
            loop();  // Resume the game loop
        }

        if (key == 'T' || key == 't') {
            sidebar.toggleTowerPlacementMode();
            towerPlacementMode = sidebar.isInTowerPlacementMode();
        } else if (key == '1') {
            sidebar.toggleRangeUpgradeMode();
        } else if (key == '2') {
            sidebar.toggleSpeedUpgradeMode();
        } else if (key == '3') {
            sidebar.toggleDamageUpgradeMode();
        } else if (key == 'M' || key == 'm') {  
            activateManaPoolSpell();
        }
    }
    
    

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
    }
    
    
    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        
        // "2x Speed" button
        if (sidebar.isSpeedToggleClicked(mouseX, mouseY)) {
            sidebar.toggleSpeedMode();
            return;
        }

        // "Pause" button
        if (sidebar.isPauseButtonClicked(mouseX, mouseY)) {
            sidebar.togglePauseMode();
            gamePaused = sidebar.isPauseActive();
            return;
        }

        // "Place Tower" button
        if (sidebar.isButtonClicked(mouseX, mouseY, 2*(sidebar.buttonHeight + 10))) {
            sidebar.toggleTowerPlacementMode();
            return;
        } 

        // "Upgrade Range" button
        if (sidebar.isButtonClicked(mouseX, mouseY, 3*(sidebar.buttonHeight + 10))) {
            sidebar.toggleRangeUpgradeMode();
            return;
        } 

        // "Upgrade Speed" button
        if (sidebar.isButtonClicked(mouseX, mouseY, 4*(sidebar.buttonHeight + 10))) {
            sidebar.toggleSpeedUpgradeMode();
            return;
        } 

        // "Upgrade Damage" button
        if (sidebar.isButtonClicked(mouseX, mouseY, 5*(sidebar.buttonHeight + 10))) {
            sidebar.toggleDamageUpgradeMode();
            return;
        } 

        // "Mana Pool Spell" button
        if (sidebar.isManaPoolSpellButtonClicked(mouseX, mouseY)) {
            activateManaPoolSpell();
            return;
        }

        int tileX = mouseX / App.CELLSIZE;
        int tileY = (mouseY - App.TOPBAR) / App.CELLSIZE;
         // Check boundaries before doing anything
        if (tileX < 0 || tileX >= board.getTiles()[0].length || tileY < 0 || tileY >= board.getTiles().length) {
            return;  // Outside of board, so just return
        }

        // First, check if the clicked tile is a tower
        Tile clickedTile = board.getTiles()[tileY][tileX];
        if (clickedTile instanceof TowerTile) {
            TowerTile tower = (TowerTile) clickedTile;
            selectedTower = tower;
    
            if (sidebar.isInRangeUpgradeMode()) {
                int upgradeCost = calculateUpgradeCost(tower.getRangeUpgradeLevel());
                if(canAfford(upgradeCost) && tower.upgradeRange()) {
                    deductMana(upgradeCost);
                }
            }
            if (sidebar.isInSpeedUpgradeMode()) {
                int upgradeCost = calculateUpgradeCost(tower.getSpeedUpgradeLevel());
                if(canAfford(upgradeCost) && tower.upgradeSpeed()) {
                    deductMana(upgradeCost);
                }
            }
            if (sidebar.isInDamageUpgradeMode()) {
                int upgradeCost = calculateUpgradeCost(tower.getDamageUpgradeLevel());
                if(canAfford(upgradeCost) && tower.upgradeDamage()) {
                    deductMana(upgradeCost);
                }
            }
    
            return;  // Return early since we have already handled the tower upgrade
        }
    


        // Next, handle tower placement with potential upgrades
        if (sidebar.isInTowerPlacementMode()) {
            int numUpgradesSelected = 0;
            if (sidebar.isInRangeUpgradeMode()) numUpgradesSelected++;
            if (sidebar.isInSpeedUpgradeMode()) numUpgradesSelected++;
            if (sidebar.isInDamageUpgradeMode()) numUpgradesSelected++;

            int towerPlacementCost = calculateTowerPlacementCost(numUpgradesSelected);
            if (mana >= towerPlacementCost) {
                TowerTile newTower = board.placeTower(tileX, tileY, this, initialTowerRange, initialTowerFiringSpeed, initialTowerDamage);
                if (newTower != null) {  // If a tower was placed
                    mana -= towerPlacementCost;  // Deduct mana
                    topBar.setMana(mana);
                    if (sidebar.isInRangeUpgradeMode()) {
                        newTower.upgradeRange();
                    }
                    if (sidebar.isInSpeedUpgradeMode()) {
                        newTower.upgradeSpeed();
                    }
                    if (sidebar.isInDamageUpgradeMode()) {
                        newTower.upgradeDamage();
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        int tileX = mouseX / App.CELLSIZE;
        int tileY = (mouseY - App.TOPBAR) / App.CELLSIZE;
    
        // Check boundaries before doing anything
        if (tileX < 0 || tileX >= board.getTiles()[0].length || tileY < 0 || tileY >= board.getTiles().length) {
            selectedTower = null; // No tower selected
            return;
        }
    
        // Check if the hovered tile is a tower
        Tile hoveredTile = board.getTiles()[tileY][tileX];
        if (hoveredTile instanceof TowerTile) {
            selectedTower = (TowerTile) hoveredTile;
        } else {
            selectedTower = null; // No tower selected
        }
    }
    

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
    }
    
    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        if (!gamePaused) {
            int updates = sidebar.isDoubleSpeedMode() ? 2 : 1;
        
            for (int u = 0; u < updates; u++) {

                // Handle waves
                if (currentWaveIndex < waves.size()) {
                    Wave currentWave = waves.get(currentWaveIndex);
                    if (waveTimer < currentWave.getPreWavePause()) {
                        // We're in the pause phase of the wave, don't update monsters
                    } else {
                        currentWave.update();
                    }
                    waveTimer += 1.0 / FPS;  // Increment timer by frame duration
                    if (waveTimer > (currentWave.getPreWavePause() + currentWave.getDuration())) {
                        waveTimer = 0;  // Reset timer
                        currentWaveIndex++;  // Move to the next wave
                        }
                    }
        
                // Update all active monsters
                for (Monster monster : activeMonsters) {
                    monster.moveWithSpeed();
                    monster.update();
                }        
        
                // Make towers shoot at monsters
                Tile[][] tiles = board.getTiles();
                for (int i = 0; i < tiles.length; i++) {
                    for (int j = 0; j < tiles[0].length; j++) {
                        if (tiles[i][j] instanceof TowerTile) {
                            TowerTile tower = (TowerTile) tiles[i][j];
                            tower.incrementTimeSinceLastShot(1.0 / FPS);
                            Monster target = tower.getClosestMonsterInRange(activeMonsters);
                            if (target != null) {
                                tower.shootMonster(target);
                            }
                            tower.updateFireballs(sidebar.isDoubleSpeedMode());   // Update the fireballs, but consider rendering outside the loop
                        }
                    }
                }
        
                // Remove dead monsters
                Iterator<Monster> monsterIterator = activeMonsters.iterator();
                while (monsterIterator.hasNext()) {
                    Monster monster = monsterIterator.next();
                    if (monster.getX() == -1.0f && monster.getY() == -1.0f) {
                        monsterIterator.remove();
                    }
                }
        
                totalGameTime += 1.0 / FPS;
                manaUpdateTimer += 1.0 / FPS;
                if (manaUpdateTimer >= 1.0f) {
                    mana += manaGainedPerSecond * manaMultiplier;  // Updated to consider multiplier
                    manaUpdateTimer -= 1.0f; // reset the timer for the next second
                }
                topBar.setMana(Math.round(mana));     
                updateWaveTimer(); 
            }

        }    
    // Check for Loss Condition
    if (mana < 0) {
        mana = 0; // Set mana to 0 for display purposes
        topBar.setMana(mana); // Update the display
        topBar.render(this);

        textSize(32);
        textAlign(CENTER, CENTER);
        fill(255, 0, 0); // Set text color to red
        text("YOU LOST", WIDTH / 2, HEIGHT / 2);
        textSize(24);
        text("Press 'r' to restart", WIDTH / 2, HEIGHT / 2 + 40); // Display instruction to restart
        textAlign(LEFT, BASELINE); // Reset the text alignment
        noLoop(); // Stop the game loop
        return; // Exit the draw function
    }

    // Check for Win Condition
    if (currentWaveIndex == waves.size() && activeMonsters.isEmpty()) {
        System.out.println("Current Wave Index: " + currentWaveIndex);
        System.out.println("Total Waves: " + waves.size());
        System.out.println("Active Monsters: " + activeMonsters.size());

        if (!isTransitioning) {
            if (config.hasKey("levels") && currentLevelIndex < levels.size() - 1) {
                isTransitioning = true;
            } else {
                textSize(32);
                textAlign(CENTER, CENTER);
                fill(0, 255, 0); // Set text color to green
                text("YOU WIN", WIDTH / 2, HEIGHT / 2);
                textAlign(LEFT, BASELINE); // Reset the text alignment
                noLoop(); // Stop the game loop
                return;  // Return here to prevent further rendering
            }
        }

        if (isTransitioning) {
            transitionTimer += 1.0 / FPS;
            textSize(32);
            textAlign(CENTER, CENTER);
            fill(0, 0, 255); // Set text color to blue
            text("Next Level in: " + Math.ceil(transitionDuration - transitionTimer), WIDTH / 2, HEIGHT / 2);
            textAlign(LEFT, BASELINE); // Reset the text alignment

            if (transitionTimer >= transitionDuration) {
                isTransitioning = false;
                transitionTimer = 0.0f;
                currentLevelIndex++;
                currentLevelConfig = levels.getJSONObject(currentLevelIndex);
                initializeLevel();
            }
            return;
        }
    }

    
    // Renderings (should be done once per frame)
    background(255);
    board.render(this);

    // Render fireballs here:
    Tile[][] tiles = board.getTiles();
    for (int i = 0; i < tiles.length; i++) {
        for (int j = 0; j < tiles[0].length; j++) {
            if (tiles[i][j] instanceof TowerTile) {
                TowerTile tower = (TowerTile) tiles[i][j];
                tower.renderFireballs();
            }
        }
    }

    for (Monster monster : activeMonsters) {
        monster.render(this);
    }  
    sidebar.render(this, selectedTower);
    topBar.render(this);
    board.renderWizardHouse(this);
    }


    public void addActiveMonster(Monster monster) {
        activeMonsters.add(monster);
    }

    private void updateWaveTimer() {
        float elapsedTime = totalGameTime;
        float timeForNextWave = 0.0f;
        
        for (int i = 0; i < waves.size(); i++) {
            float waveTotalTime = waves.get(i).getPreWavePause() + (i == waves.size() - 1 ? 0 : waves.get(i).getDuration());
            
            if (i == 0) {
                // For the first wave
                timeForNextWave = waves.get(i).getPreWavePause();
            } else {
                // For subsequent waves
                timeForNextWave = waves.get(i).getPreWavePause() + waves.get(i - 1).getDuration();
            }
            
            if (elapsedTime < timeForNextWave) {
                topBar.setWaveTimer(i + 1, timeForNextWave - elapsedTime);
                return;
            } else {
                elapsedTime -= timeForNextWave;
            }
        }
    
        // All waves completed
        topBar.setWaveTimer(-1, 0);
    }

    public void addMana(float amount) {
        this.mana += amount;
    }

    public void deductMana(int cost) {
        mana -= cost;
    }
    

    private void activateManaPoolSpell() {
        if (mana >= currentManaPoolSpellCost) {
            mana -= currentManaPoolSpellCost;
            initialManaCap *= manaPoolSpellCapMultiplier;
            topBar.setManaCap(initialManaCap);
            manaMultiplier += manaPoolSpellManaGainedMultiplier;  // Increase the mana multiplier
            currentManaPoolSpellCost += manaPoolSpellCostIncreasePerUse;
        }
    }

    public float getCurrentManaMultiplier() {
        return manaMultiplier;
    }

    // Calculate the cost to place a tower with the given number of upgrades
    public int calculateTowerPlacementCost(int numUpgrades) {
        return towerBaseCost + numUpgrades * 20;  // 100 is the base cost of placing a tower, each upgrade costs 20 more
    }

    // Calculate the upgrade cost for a tower based on the current upgrade level
    public int calculateUpgradeCost(int currentUpgradeLevel) {
        return 20 + currentUpgradeLevel * 10;  // 20 is the base cost for the first upgrade, subsequent upgrades cost 10 more
    }

    public boolean canAfford(int cost) {
        return mana >= cost;
    }

    public float getCurrentManaPoolSpellCost() {
        return currentManaPoolSpellCost;
    }

    
    
    
    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
