package invaders.entities;

import invaders.logic.Damagable;
import invaders.physics.Moveable;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import javafx.scene.image.Image;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-29
 * Time: 15:02
 */
public class Projectile implements Moveable, Damagable, Renderable {
    private static final double WIDTH = 25;
    private static final double HEIGHT = 30;

    public enum Type {
        SLOW("src/main/resources/blue_projectile.PNG"), FAST("src/main/resources/red_projectile.PNG");
        private Image image;

        Type(String image) {
            this.image = new Image(new File(image).toURI().toString(), WIDTH, HEIGHT, true, true);
        }
    }

    interface MoveStrategy {
        double SPEED = 1;

        double move();

        Image getImage();
    }

    class FastMove implements MoveStrategy {
        @Override
        public double move() {
            return 2 * SPEED;
        }

        @Override
        public Image getImage() {
            return Type.FAST.image;
        }
    }

    class SlowMove implements MoveStrategy {
        @Override
        public double move() {
            return SPEED;
        }

        public Image getImage() {
            return Type.SLOW.image;
        }
    }

    private final Vector2D position;
    private boolean alive = true;

    private MoveStrategy moveStrategy;
    private boolean isPlayer;

    public Projectile(Vector2D position, Type type, boolean isPlayer) {
        this.position = position;
//        position.setX(position.getX() - WIDTH / 2);
        this.isPlayer = isPlayer;

        // The behaviour of enemy projectiles will be controlled using the Strategy pattern.
        if (type.equals(Type.FAST)) {
            moveStrategy = new FastMove();
        } else {
            moveStrategy = new SlowMove();
        }
    }

    @Override
    public void takeDamage(double amount) {
        this.alive = false;
    }

    @Override
    public double getHealth() {
        return alive ? 1 : 0;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public void up() {
        if (isPlayer) {
            this.position.setY(this.position.getY() - moveStrategy.move());
        }
    }

    @Override
    public void down() {
        if (!isPlayer) {
            this.position.setY(this.position.getY() + moveStrategy.move());
        }
    }

    @Override
    public void left() {
    }

    @Override
    public void right() {
    }

    @Override
    public Image getImage() {
        return moveStrategy.getImage();
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    public boolean isPlayer() {
        return isPlayer;
    }
}
