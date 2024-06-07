package WizardTD;

import processing.core.PApplet;

public class StaticBox {
  private int x;
  private int y;
  private int width;
  private int height;
  private int borderThickness;
  private int fillColor;
  private int borderColor;

  public StaticBox(int x, int y, int width, int height, int borderThickness, int fillColor, int borderColor) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.borderThickness = borderThickness;
    this.fillColor = fillColor;
    this.borderColor = borderColor;
  }

  public void draw(PApplet app) {
    app.stroke(borderColor);
    app.strokeWeight(borderThickness);
    app.fill(fillColor);
    app.rect(x, y, width, height);
  }
}
