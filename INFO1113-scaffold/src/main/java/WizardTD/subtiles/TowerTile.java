package WizardTD.subtiles;

import WizardTD.*;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TowerTile extends Tile {
    protected PImage image;
    protected int rangeUpgradeLevel = 0;
    protected int speedUpgradeLevel = 0;
    protected int damageUpgradeLevel = 0;
    protected PApplet app;
    protected int towerAppearanceState = 0; // 0 for initial, 1 for orange, 2 for red
    protected float towerRange;
    protected float towerFiringSpeed;
    protected float towerDamage;
    protected float initialTowerDamage;
    protected float timeSinceLastShot = 0;  // To control the firing rate

    protected int x;  // x coordinate
    protected int y;  // y coordinate
    protected List<Fireball> fireballs = new ArrayList<>();

    public TowerTile(PApplet app, int x, int y, float towerRange, float towerFiringSpeed, float towerDamage) {
        this.app = app;
        this.x = x;
        this.y = y;
        image = app.loadImage("src/main/resources/WizardTD/tower0.png");
        this.initialTowerDamage = towerDamage;
        this.towerRange = towerRange;
        this.towerFiringSpeed = towerFiringSpeed;
        this.towerDamage = towerDamage;
    }

    public int getCost() {
        int cost = 100;  // Base cost from config
        int upgradeCount = rangeUpgradeLevel + speedUpgradeLevel + damageUpgradeLevel;
        cost += 20 * upgradeCount;  // Add 20 for each upgrade
        return cost;
    }

    public int getUpgradeCost(int currentLevel) {
        return 20 + (currentLevel * 10); // 20 for level 1, 30 for level 2, and so on...
    }
    
    

    public Monster getClosestMonsterInRange(List<Monster> monsters) {
        Monster closestMonster = null;
        float minDistance = Float.MAX_VALUE;
    
        for (Monster monster : monsters) {
            float distance = PApplet.dist(x * App.CELLSIZE, y * App.CELLSIZE, monster.getX() * App.CELLSIZE, monster.getY() * App.CELLSIZE);
            if (distance < towerRange && distance < minDistance) {
                closestMonster = monster;
                minDistance = distance;
            }
        }
        return closestMonster;
    }

    public void shootMonster(Monster monster) {
        if (monster.getCurrentHp() <= 0) {
            return; // Do not shoot at dead monsters.
        }
        if (timeSinceLastShot >= 1.0 / towerFiringSpeed) {
            fireballs.add(new Fireball((x * App.CELLSIZE) + (App.CELLSIZE / 2.0f), (y * App.CELLSIZE) + (App.CELLSIZE / 2.0f) + App.TOPBAR, monster, app));
            timeSinceLastShot = 0;
        }
    }
    

    public void updateFireballs(boolean doubleSpeed) {
        Iterator<Fireball> iterator = fireballs.iterator();
        while (iterator.hasNext()) {
            Fireball fireball = iterator.next();
            fireball.update(doubleSpeed);  // Pass the doubleSpeed value
            
            if (fireball.getTarget() != null && fireball.hasHitTarget()) {
                fireball.getTarget().reduceHealth(towerDamage);
                iterator.remove();
            }
        }
    }

    public void renderFireballs() {
        for (Fireball fireball : fireballs) {
            fireball.render();
        }
    }

    public void incrementTimeSinceLastShot(double increment) {
        this.timeSinceLastShot += increment;
    }

    public boolean upgradeRange() {
        if(app instanceof App) {
            App gameApp = (App) app;
            int upgradeCost = gameApp.calculateUpgradeCost(rangeUpgradeLevel);
            if(gameApp.canAfford(upgradeCost)) {
                rangeUpgradeLevel++;
                towerRange += 32;  // Increase tower range by 32 pixels
                updateTowerImage();
                return true;
            }
        }
        return false;
    }
    
    public boolean upgradeSpeed() {
        if(app instanceof App) {
            App gameApp = (App) app;
            int upgradeCost = gameApp.calculateUpgradeCost(speedUpgradeLevel);
            if(gameApp.canAfford(upgradeCost)) {
                speedUpgradeLevel++;
                towerFiringSpeed += 0.5f;  // Increase firing speed by 0.5 fireballs per second
                updateTowerImage();
                return true;
            }
        }
        return false;
    }
    
    public boolean upgradeDamage() {
        if(app instanceof App) {
            App gameApp = (App) app;
            int upgradeCost = gameApp.calculateUpgradeCost(damageUpgradeLevel);
            if(gameApp.canAfford(upgradeCost)) {
                damageUpgradeLevel++;
                towerDamage += initialTowerDamage / 2.0f;  // Increase damage by half of initial damage
                updateTowerImage();
                return true;
            }
        }
        return false;
    }

    public int getRangeUpgradeLevel() {
        return rangeUpgradeLevel;
    }
    
    public int getSpeedUpgradeLevel() {
        return speedUpgradeLevel;
    }
    
    public int getDamageUpgradeLevel() {
        return damageUpgradeLevel;
    }
    
    

    protected void updateTowerImage() {
        if (rangeUpgradeLevel >= 2 && speedUpgradeLevel >= 2 && damageUpgradeLevel >= 2) {
            image = app.loadImage("src/main/resources/WizardTD/tower2.png");
            towerAppearanceState = 2;
        } else if (rangeUpgradeLevel >= 1 && speedUpgradeLevel >= 1 && damageUpgradeLevel >= 1) {
            image = app.loadImage("src/main/resources/WizardTD/tower1.png");
            towerAppearanceState = 1;
        } else {
            image = app.loadImage("src/main/resources/WizardTD/tower0.png");
            towerAppearanceState = 0;
        }
    }

    public boolean isMouseHovering(int mouseX, int mouseY) {
        float towerCenterX = x * App.CELLSIZE + App.CELLSIZE / 2.0f;
        float towerCenterY = y * App.CELLSIZE + App.CELLSIZE / 2.0f;
        
        return PApplet.dist(towerCenterX, towerCenterY, mouseX, mouseY) <= App.CELLSIZE / 2.0f;
    }

    public int getNextRangeUpgradeCost() {
        return getUpgradeCost(rangeUpgradeLevel);
    }
    
    public int getNextSpeedUpgradeCost() {
        return getUpgradeCost(speedUpgradeLevel);
    }
    
    public int getNextDamageUpgradeCost() {
        return getUpgradeCost(damageUpgradeLevel);
    }

    public void despawnFireballsTargeting(Monster monster) {
        Iterator<Fireball> iterator = fireballs.iterator();
        while (iterator.hasNext()) {
            Fireball fireball = iterator.next();
            if (fireball.getTarget() == monster) {
                iterator.remove();
            }
        }
    }
    
    

    @Override
    public void render(int x, int y, PApplet app) {
        app.image(image, x, y);
        
        float textSize = App.CELLSIZE / 4;  // Reduce the size of the text by half
        app.textSize(textSize);

        
        // Render range upgrade level
        if (rangeUpgradeLevel > towerAppearanceState) {
            app.fill(app.color(255, 0, 255));  // Magenta color
            for (int i = 0; i < rangeUpgradeLevel - towerAppearanceState; i++) {
                app.text("O", x + (i * textSize), y + textSize);
            }
        }

        // Check if the mouse is hovering over this tower
        if (isMouseHovering(app.mouseX, app.mouseY - App.TOPBAR)) {
            app.noFill();
            app.stroke(255, 255, 0);  // Yellow color
            app.ellipse(x + App.CELLSIZE / 2.0f, y + App.CELLSIZE / 2.0f, towerRange * 2, towerRange * 2);  // Draw the range
            app.noStroke();  // Reset the stroke
        }

        // Render speed upgrade level
        if (speedUpgradeLevel > 0) {
            app.stroke(173, 216, 230);  // Lighter blue color
            for (int i = 0; i < speedUpgradeLevel - towerAppearanceState; i++) {
                app.strokeWeight(1);  // Keeping a constant stroke weight
                float radiusReduction = i * 4; // Adjust this value to change the gap between rings
                app.noFill();
                app.ellipse((float)x + App.CELLSIZE / 2.0f, (float)y + App.CELLSIZE / 2.0f, App.CELLSIZE / 2.0f - radiusReduction, App.CELLSIZE / 2.0f - radiusReduction);
            }
            app.noStroke();
        }

        
        // Render damage upgrade level
        if (damageUpgradeLevel > towerAppearanceState) {
            app.fill(app.color(255, 0, 255));  // Magenta color
            for (int i = 0; i < damageUpgradeLevel - towerAppearanceState; i++) {
                app.text("X", x + (i * textSize), y + App.CELLSIZE - textSize / 2);
            }
        }
        
        app.textSize(App.CELLSIZE);  // Reset text size
    }
    public PImage getImage() {
        return image;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public List<Fireball> getFireballs() {
        return fireballs;
    }
    
    public float getTowerFiringSpeed() {
        return towerFiringSpeed;
    }
    
    public float getTowerDamage() {
        return towerDamage;
    }

    public float getTowerRange() {
        return towerRange;
    }
    
    
}
