package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

public class Fireball {
    float x, y;  // Current position
    float dx, dy;  // x and y increments
    float speed = 5;  // Fireball speed
    Monster target;  // Target monster
    PImage fireballImage;
    PApplet app;

    public Fireball(float startX, float startY, Monster target, PApplet app) {
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.app = app;

        // Load fireball image
        fireballImage = app.loadImage("src/main/resources/WizardTD/fireball.png");
        initialTrajectory();
    }

    private void initialTrajectory() {
        // Initial prediction of where the monster will be after a certain time
        float distanceToMonster = PApplet.dist(x, y, target.getX() * App.CELLSIZE, target.getY() * App.CELLSIZE);
        float predictedTime = distanceToMonster / speed;
        float predictedMonsterX = target.getX() * App.CELLSIZE + target.getSpeed() * predictedTime * PApplet.cos(PApplet.radians(target.getDirectionAngle()));
        float predictedMonsterY = target.getY() * App.CELLSIZE + target.getSpeed() * predictedTime * PApplet.sin(PApplet.radians(target.getDirectionAngle()));
        
        // Adjust trajectory towards the predicted position
        float angle = PApplet.atan2(predictedMonsterY - y, predictedMonsterX - x);
        dx = speed * PApplet.cos(angle);
        dy = speed * PApplet.sin(angle);
    }

    public void update(boolean doubleSpeed)  {
        // Homing logic: Adjust the trajectory every frame to aim towards the monster's current position
        float angle = PApplet.atan2(target.getY() * App.CELLSIZE + App.CELLSIZE / 2 - y, target.getX() * App.CELLSIZE + App.CELLSIZE / 2 - x);
        float adjustedSpeed = doubleSpeed ? speed * 2 : speed;
        dx = adjustedSpeed * PApplet.cos(angle);
        dy = adjustedSpeed * PApplet.sin(angle);
    
        x += dx;
        y += dy;
    }

    public boolean hasHitTarget() {
        float targetCenterX = target.getX() * App.CELLSIZE + App.CELLSIZE / 2;
        float targetCenterY = target.getY() * App.CELLSIZE + App.CELLSIZE / 2;
        float monsterHitboxRadius = target.getImage().width * 1.0f / 2;  // Using 100% of monster's width for the hitbox
        return PApplet.dist(x, y, targetCenterX, targetCenterY) <= (fireballImage.width / 2 + monsterHitboxRadius);
    }
    


    public Monster getTarget() {
        return this.target;
    }


    public void render() {
        app.image(fireballImage, x - fireballImage.width / 2, y - fireballImage.height / 2);
    }
}
