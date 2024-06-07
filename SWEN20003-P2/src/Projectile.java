import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Vector2;

public class Projectile {
    private static final int SPEED = 6;
    private int x, y;
    private Image image;
    private double speedX;
    private double speedY;
    private DrawOptions drawOptions = new DrawOptions();
    private boolean active = true;

    public Projectile(int x, int y, Enemy enemy) {
        this.x = x;
        this.y = y;
        this.image = new Image("res/arrow.PNG");

        //正右方向是0， 朝下是pi/2，朝左是pi,朝上是-pi/2
        double xDifference = enemy.getPoint().x - x;
        double yDifference = enemy.getPoint().y - y;
        drawOptions.setRotation(Math.atan2(yDifference, xDifference));

        double length = Math.sqrt(xDifference * xDifference + yDifference * yDifference);
        speedX = xDifference / length * SPEED;
        speedY = yDifference / length * SPEED;
    }

    public void update() {
        x += speedX;
        y += speedY;
        if (x <= 0 || y <= 0 || x >= 1024 || y >= 768) {
            active = false;
        }
        draw();
    }

    public void draw() {
        if (active) {
            image.draw(x, y, drawOptions);
        }
    }

    /**
     * If the distance between the centre-coordinates of the enemy image and
     * the centre-coordinates of the projectile image is <= 62, this is considered as a collision
     * @param enemy
     * @return
     */
    public boolean collision(Enemy enemy) {
        double detaX = enemy.getPoint().x - x;
        double detaY = enemy.getPoint().y - y;
        double distance = Math.sqrt(detaX * detaX + detaY * detaY);
        return distance <= 62;
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}
