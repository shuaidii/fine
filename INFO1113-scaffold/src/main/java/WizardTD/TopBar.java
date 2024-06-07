package WizardTD;

import processing.core.PApplet;

public class TopBar {
    protected int width;
    protected int height;
    protected int mana;
    protected int manaCap;

    protected int waveNumber = -1;
    protected float timeRemaining = 0.0f;

    public TopBar(int width, int height, int initialMana, int manaCap) {
        this.width = width;
        this.height = height;
        this.mana = initialMana;
        this.manaCap = manaCap;
    }

    public void setWaveTimer(int waveNumber, float timeRemaining) {
        this.waveNumber = waveNumber;
        this.timeRemaining = timeRemaining;
    }


    public void render(PApplet app) {
        // Set color and draw the top bar background
        app.fill(app.color(0x83, 0x74, 0x4A));
        app.rect(0, 0, this.width, this.height);
        
        // Set the position and size for the mana bar
        int manaBarWidth = 320;
        int manaBarHeight = 16;
        int manaBarX = this.width - manaBarWidth - 32;  // 32 pixels (1 tile) away from the right edge
        int manaBarY = (this.height - manaBarHeight) / 2;  // Centered vertically
        
        // Adjusted the text's x position to shift "MANA :" to the left
        app.fill(0);  // Black color for text
        app.textSize(18);
        app.text("MANA :", manaBarX - 74, manaBarY + 13);  // Adjusted y position to align text vertically with the bar
        
        // Draw the background for the mana bar (white)
        app.fill(255);
        app.noStroke();
        app.rect(manaBarX, manaBarY, manaBarWidth, manaBarHeight);
        
        // Draw the actual mana bar with the specified color
        app.fill(app.color(0x03, 0xD6, 0xD6));
        float manaPercentage = (float) mana / manaCap;
        app.rect(manaBarX + 2, manaBarY + 2, manaPercentage * (manaBarWidth - 4), manaBarHeight - 3);  // Adjusted height to fill white space
        
        // Draw the border around the mana bar
        app.stroke(0);
        app.strokeWeight(3);
        app.noFill();
        app.rect(manaBarX, manaBarY, manaBarWidth, manaBarHeight);
        
        // Reset stroke weight and disable stroke for other elements
        app.strokeWeight(1);
        app.noStroke();
        
        // Display the mana value in the center of the mana bar
        String manaText = mana + "/" + manaCap;
        app.fill(0);  // Black color for text
        app.textSize(16);  // Reduced text size
        float textWidth = app.textWidth(manaText);
        app.text(manaText, manaBarX + (manaBarWidth - textWidth) / 2, manaBarY + 14);  // Slightly adjusted y-coordinate
        
        // Render wave timer
        if (waveNumber > 0 && timeRemaining >= 0) {
            app.textSize(20);  // Increased text size for wave timer
            String waveText = "Wave " + waveNumber + " starts: " + Math.round(timeRemaining);
            app.text(waveText, 16, manaBarY + 13);
        }
    }
    
    
    public void updateMana(float amount) {
        this.mana += amount;
        if (this.mana > manaCap) {
            this.mana = manaCap;
        }
    }

    public void setMana(int mana) {
        this.mana = mana;
        if (this.mana > manaCap) {
            this.mana = manaCap;
        }
    }
    
    public int getMana() {
        return this.mana;
    }


    public int getManaCap() {
        return this.manaCap;
    }


    public void setManaCap(int newManaCap) {
        this.manaCap = newManaCap;
    }
    
    
}



