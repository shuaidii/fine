import bagel.Image;
import bagel.util.Point;

public class Enemy {
    private int speed = 1;
    private int x;
    private int y;

    private Image image;
    private boolean active = false;

    public Enemy(int x, int y, boolean towardRight) {
        this.x = x;
        this.y = y;
        this.speed = towardRight ? 1 : -1;
        this.image = new Image("res/enemy.PNG");
        this.active = true;
    }

    public void update() {
        x += speed;
        //x坐标 + speed
        //随机选择一个方向
        if (x >= 900 || x <= 100) {
            speedChange();
        }
        draw();
    }

    private void speedChange() {
        if (speed == 1) {
            speed = -1;
        } else {
            speed = 1;
        }
    }

    public void draw() {
        if (active) {
            image.draw(x, y);
        }
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}
