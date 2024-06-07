package WizardTD;

import processing.core.PApplet;
import processing.core.PFont;
import processing.data.JSONArray;

public class StaticText {
    // Set text properties
    //textFont(createFont("Arial", 20));
    //textSize(20);
    //fill(0); // Text color (black)
    
    // Draw the text directly on the main canvas
    //text("Hello", 20, 40); // Text at (20, 40)

    public int x;
    public int y;
    public int fontSize;
    public String text;
    public String textFont;


    public StaticText(int x, int y, int fontSize, String text, String textFont) {
        this.x = x;
        this.y = y;
        this.fontSize = fontSize;
        this.text = text;
        this.textFont = textFont; 
    }

    public void draw(PApplet app, JSONArray colour) {
        int r = colour.getInt(0);
        int g = colour.getInt(1);
        int b = colour.getInt(2);
        PFont curTextFont = app.createFont(textFont, fontSize);
        app.textFont(curTextFont);
        app.fill(r, g, b);
        app.text(text, x, y);
    }
}
