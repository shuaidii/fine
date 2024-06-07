package WizardTD;

import processing.core.PApplet;
import WizardTD.subtiles.TowerTile;

public class Sidebar {
    protected int width;
    protected int height;

    protected boolean towerPlacementMode = false;
    protected boolean rangeUpgradeMode = false;
    protected boolean speedUpgradeMode = false;
    protected boolean damageUpgradeMode = false;
    protected boolean speedToggleActive = false;
    protected boolean pauseActive = false;



    public int buttonWidth = 40;
    public int buttonHeight = 40;
    protected int buttonX;
    protected int buttonY;
    protected App mainApp;


    public Sidebar(int width, int height, App app) {
        this.width = width;
        this.height = height;
        this.buttonX = App.WIDTH - this.width + 10;
        this.buttonY = App.TOPBAR + 10;
        this.mainApp = app;
    }

    public void toggleTowerPlacementMode() {
        towerPlacementMode = !towerPlacementMode;
    }

    public boolean isInTowerPlacementMode() {
        return towerPlacementMode;
    }

    public void toggleRangeUpgradeMode() {
        rangeUpgradeMode = !rangeUpgradeMode;
        System.out.println("Range upgrade mode toggled: " + rangeUpgradeMode);  // Debugging line
    }

    public boolean isInRangeUpgradeMode() {
        return rangeUpgradeMode;
    }

    public void toggleSpeedUpgradeMode() {
        speedUpgradeMode = !speedUpgradeMode;
        System.out.println("Speed upgrade mode toggled: " + speedUpgradeMode);  // Debugging line
    }

    public boolean isInSpeedUpgradeMode() {
        return speedUpgradeMode;
    }
    public void toggleDamageUpgradeMode() {
        damageUpgradeMode = !damageUpgradeMode;
        System.out.println("Damage upgrade mode toggled: " + damageUpgradeMode);  // Debugging line
    }
    

    public boolean isInDamageUpgradeMode() {
        return damageUpgradeMode;
    }

    public boolean isInAnyUpgradeMode() {
        return isInRangeUpgradeMode() || isInSpeedUpgradeMode() || isInDamageUpgradeMode();
    }

    public boolean isManaPoolSpellButtonClicked(int mouseX, int mouseY) {
        return isButtonClicked(mouseX, mouseY, 6 * (buttonHeight + 10));
    }

    public boolean isSpeedToggleClicked(int mouseX, int mouseY) {
        return isButtonClicked(mouseX, mouseY, 0);
    }

    public void toggleSpeedMode() {
        speedToggleActive = !speedToggleActive;
        System.out.println("Toggled speed mode to: " + speedToggleActive);
    }

    public boolean isDoubleSpeedMode() {
        return speedToggleActive;
    }

    public boolean isButtonHovered(int mouseX, int mouseY, int yOffset) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
               mouseY >= buttonY + yOffset && mouseY <= buttonY + yOffset + buttonHeight;
    }

    public boolean isPauseButtonClicked(int mouseX, int mouseY) {
        return isButtonClicked(mouseX, mouseY, 1 * (buttonHeight + 10));
    }

    public void togglePauseMode() {
        pauseActive = !pauseActive;
    }

    public boolean isPauseActive() {
        return pauseActive;
    }

    
        

    protected void renderUpgradeCosts(PApplet app, TowerTile selectedTower) {
        int startX = app.width - this.width + 10;  // Adjust to the left side of the sidebar
        int startY = app.height - 110; // Adjust starting position at bottom left
        int boxWidth = 100;  // Reduced by 20 to make it slimmer
        
        app.textSize(12);  // Make text slightly smaller
        
        // Top box: "Upgrade Costs"
        app.fill(255);  // White color
        app.rect(startX, startY, boxWidth, 20);
        app.stroke(0);  // black color
        app.noFill();
        app.rect(startX, startY, boxWidth, 20);
        startY += 20; // Move startY down after the "Upgrade Costs" box
    
        int totalCost = 0;
        int yOffset = 0; // Adjust for each piece of text
    
        StringBuilder upgradeDetails = new StringBuilder();
    
        if (isInRangeUpgradeMode()) {
            int cost = selectedTower.getNextRangeUpgradeCost();
            upgradeDetails.append("Range: ").append(cost).append("\n");
            totalCost += cost;
            yOffset += 20;
        }
        if (isInSpeedUpgradeMode()) {
            int cost = selectedTower.getNextSpeedUpgradeCost();
            upgradeDetails.append("Speed: ").append(cost).append("\n");
            totalCost += cost;
            yOffset += 20;
        }
        if (isInDamageUpgradeMode()) {
            int cost = selectedTower.getNextDamageUpgradeCost();
            upgradeDetails.append("Damage: ").append(cost).append("\n");
            totalCost += cost;
            yOffset += 20;
        }
    
        // Middle box: Individual upgrade costs
        app.fill(255);  // White color
        app.rect(startX, startY, boxWidth, yOffset);
        app.stroke(0);  // black color
        app.noFill();
        app.rect(startX, startY, boxWidth, yOffset);
        startY += yOffset;  // Move startY down after the individual costs box
    
        // Bottom box: Total
        app.fill(255);  // White color
        app.rect(startX, startY, boxWidth, 20);
        app.stroke(0);  // black color
        app.noFill();
        app.rect(startX, startY, boxWidth, 20);
    
        // Draw texts after all rectangles
        app.fill(0);  // Black text color
    
        // "Upgrade Costs" text
        app.text("Upgrade Costs", startX + 10, app.height - 110 + 15 - 3);
    
        // Individual upgrade costs text
        float textHeight = app.textAscent() + app.textDescent();  // Calculate text height
        app.text(upgradeDetails.toString(), startX + 10, app.height - 110 + 20 + 10 + (yOffset - upgradeDetails.toString().split("\n").length * textHeight) / 2);
    
        // "Total" text
        app.text("Total: " + totalCost, startX + 10, app.height - 110 + 20 + yOffset + 15 - 3);
    
        // Reset graphics settings
        app.noStroke();
        app.fill(255);
    }

    
    protected void renderManaPoolSpellCost(PApplet app) {
        if (isButtonHovered(app.mouseX, app.mouseY, 6 * (buttonHeight + 10))) {
            int costBoxWidth = 65;  // Reduced width
            int costBoxHeight = 25;  // Reduced height
            int startX = buttonX - costBoxWidth - 20;  // Shifted further to the left
            int startY = buttonY + 6 * (buttonHeight + 10);
        
            app.fill(app.color(255));  // White color
            app.rect(startX, startY, costBoxWidth, costBoxHeight);
            
            // Add a thin black outline
            app.stroke(0);
            app.strokeWeight(1);
            app.noFill();
            app.rect(startX, startY, costBoxWidth, costBoxHeight);
            
            app.noStroke();  // Reset stroke
            app.fill(0);  // Text color (black)
            app.textSize(12);  // Reduced font size
            app.text("Cost: " + (int)mainApp.getCurrentManaPoolSpellCost(), startX + 5, startY + 17);  // Adjusted text position
        }
    }
    

    // Check which button was clicked
    public boolean isButtonClicked(int mouseX, int mouseY, int yOffset) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
               mouseY >= buttonY + yOffset && mouseY <= buttonY + yOffset + buttonHeight;
    }

    protected void renderTowerCost(PApplet app) {
        if (isButtonHovered(app.mouseX, app.mouseY, 2*(buttonHeight + 10))) {
            int costBoxWidth = 65;  // Reduced width
            int costBoxHeight = 25;  // Reduced height
            int startX = buttonX - costBoxWidth - 20;  // Shifted further to the left
            int startY = buttonY + 2 * (buttonHeight + 10);
        
            app.fill(app.color(255));  // White color
            app.rect(startX, startY, costBoxWidth, costBoxHeight);
            
            // Add a thin black outline
            app.stroke(0);
            app.strokeWeight(1);
            app.noFill();
            app.rect(startX, startY, costBoxWidth, costBoxHeight);
            
            app.noStroke();  // Reset stroke
            app.fill(0);  // Text color (black)
            app.textSize(12);  // Reduced font size
            app.text("Cost: " + mainApp.towerBaseCost, startX + 5, startY + 17);  // Adjusted text position
        }
    }
    
    // Update render method:
    public void render(PApplet app, TowerTile selectedTower) {
        app.noTint();
        app.fill(app.color(0x83, 0x74, 0x4A));
        app.rect(app.width - this.width, App.TOPBAR, this.width, this.height - App.TOPBAR);

        renderButton(app, "FF", 0, speedToggleActive); 
        renderDescription(app, "2 x speed", 0);
        
        renderButton(app, "P", buttonHeight + 10, pauseActive);
        renderDescription(app, "PAUSE", buttonHeight + 10);
        
        renderButton(app, "T", 2*(buttonHeight + 10), towerPlacementMode);
        renderDescription(app, "Build\ntower", 2*(buttonHeight + 10));
        
        renderButton(app, "U1", 3*(buttonHeight + 10), rangeUpgradeMode);
        renderDescription(app, "Upgrade\nrange", 3*(buttonHeight + 10));
        
        renderButton(app, "U2", 4*(buttonHeight + 10), speedUpgradeMode);
        renderDescription(app, "Upgrade\nspeed", 4*(buttonHeight + 10));
        
        renderButton(app, "U3", 5*(buttonHeight + 10), damageUpgradeMode);
        renderDescription(app, "Upgrade\ndamage", 5*(buttonHeight + 10));
        
        renderButton(app, "M", 6 * (buttonHeight + 10), false);
        renderDescription(app, "Mana pool\ncost: " + (int)mainApp.getCurrentManaPoolSpellCost(), 6*(buttonHeight + 10));

        renderManaPoolSpellCost(app);
        renderTowerCost(app);  

        if (selectedTower != null && isInAnyUpgradeMode()) {
            renderUpgradeCosts(app, selectedTower);
        }
    }

    protected void renderDescription(PApplet app, String description, int yOffset) {
        app.fill(0);  // Black color for text
        app.textSize(12);  // Smaller text size for descriptions
        float descX = buttonX + buttonWidth + 5;  // Right of the button
        float descY = buttonY + 15 + yOffset;  // Vertically align with the top of the button
        app.text(description, descX, descY);
    }
    
    protected void renderButton(PApplet app, String text, int yOffset, boolean active) {
        if (active) {
            app.fill(app.color(0xfb, 0xfb, 0x0d));
        } else if (isButtonHovered(app.mouseX, app.mouseY, yOffset)) {
            app.fill(app.color(200, 200, 200));  // Gray color when hovered
        } else {
            app.fill(app.color(0x83, 0x74, 0x4A));
        }        
        app.rect(buttonX, buttonY + yOffset, buttonWidth, buttonHeight);

        app.stroke(0);
        app.strokeWeight(2);
        app.noFill();
        app.rect(buttonX, buttonY + yOffset, buttonWidth, buttonHeight);
        app.noStroke();

        app.fill(0);
        app.textSize(App.CELLSIZE * 0.8f);  // Decreased text size
        float textWidth = app.textWidth(text);
        float textX = buttonX + (buttonWidth - textWidth) / 2;   // Center text horizontally
        float textY = buttonY + 28 + yOffset;  // Adjusted for vertical alignment
        app.text(text, textX, textY);
    }
}
