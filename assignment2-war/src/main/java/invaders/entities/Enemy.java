package invaders.entities;

import invaders.engine.GameEngine;
import invaders.engine.GameWindow;
import invaders.logic.Damagable;
import invaders.physics.Moveable;
import invaders.physics.Vector2D;
import invaders.rendering.Animator;
import invaders.rendering.Renderable;
import javafx.scene.image.Image;

import java.io.File;
import java.util.Random;

public class Enemy implements Moveable, Damagable, Renderable {
    public static double speedRate = 1.0;
    private static final double SPEED = 1;
    private static final double DOWN_STEP = 5;
    private static final Random RANDOM = new Random();

    private final Vector2D position;
    private double health = 1;

    private final double width = 25;
    private final double height = 30;
    private final Image image;
    private Projectile.Type projetileType;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    private Enemy(Vector2D position, Projectile.Type projetileType) {
        this.image = new Image(new File("src/main/resources/enemy.png").toURI().toString(), width, height, true, true);
        this.position = position;
        this.projetileType = projetileType;
        if (RANDOM.nextBoolean()) {
            moveLeft = true;
        } else {
            moveRight = true;
        }
    }

    @Override
    public void takeDamage(double amount) {
        this.health -= amount;
    }

    @Override
    public double getHealth() {
        return this.health;
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    @Override
    public void up() {
        return;
    }

    @Override
    public void down() {
        if (position.getX() <= 1 || position.getX() + width >= GameWindow.width - 1) {
            position.setY(position.getY() + DOWN_STEP);
            if (position.getX() <= 0) {
                moveLeft = false;
                moveRight = true;
            } else {
                moveLeft = true;
                moveRight = false;
            }
        }
    }

    @Override
    public void left() {
        if (moveLeft) {
            this.position.setX(this.position.getX() - SPEED * speedRate);
        }
    }

    @Override
    public void right() {
        if (moveRight) {
            this.position.setX(this.position.getX() + SPEED * speedRate);
        }
    }

    public Projectile shoot() {
//        System.out.println("Enemy shoot: " + projetileType);
        return new Projectile(new Vector2D(position.getX() + width / 2 - 1, position.getY()), projetileType, false);
    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public Layer getLayer() {
        return Layer.FOREGROUND;
    }

    public static class EnemyBuilder {
        private Vector2D vector2D;
        Projectile.Type projetileType;

        public EnemyBuilder vector2D(Vector2D vector2D) {
            this.vector2D = vector2D;
            return this;
        }

        public EnemyBuilder projetileType(Projectile.Type projetileType) {
            this.projetileType = projetileType;
            return this;
        }

        public static EnemyBuilder builder() {
            return new EnemyBuilder();
        }

        public Enemy build() {
            return new Enemy(vector2D, projetileType);
        }
    }


}
