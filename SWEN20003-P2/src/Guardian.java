import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.awt.*;
import java.util.List;

public class Guardian {
    private int x;
    private int y;
    private Image image;


    public Guardian(int x, int y) {
        this.x = x;
        this.y = y;
        this.image = new Image("res/guardian.PNG");
    }

    public void update() {
        draw();
    }

    public Projectile checkShoot(Input input, Enemy enemy) {
        if (enemy == null || !input.wasPressed(Keys.LEFT_SHIFT)) {
            return null;
        }
        return new Projectile(x, y, enemy);
    }

    public void draw() {
        image.draw(x, y);
    }

    public Point getPoint() {
        return new Point(x, y);
    }
}
