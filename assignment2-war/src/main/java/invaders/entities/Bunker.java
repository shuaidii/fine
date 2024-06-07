package invaders.entities;

import invaders.logic.Damagable;
import invaders.physics.Vector2D;
import invaders.rendering.Animation;
import invaders.rendering.Animator;
import invaders.rendering.Renderable;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-09-29
 * Time: 15:16
 */
public class Bunker implements Damagable, Renderable {
    static class BunkerAnimation implements Animation {
        private String name;
        private Image image;
        private Animation next;

        public BunkerAnimation(String name, Image image, Animation next) {
            this.name = name;
            this.image = image;
            this.next = next;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Image getCurrentFrame() {
            return image;
        }

        @Override
        public Animation next() {
            return next;
        }
    }

    private Vector2D position;

    private double width;
    private double height;

    private Layer layer = Layer.EFFECT;
    private Animator animator;
    private boolean alive = true;


    private Bunker(Vector2D position, double width, double height) {
        this.position = position;
        this.width = width;
        this.height = height;
        initAnimator();
    }

    /**
     * Colour change of each bunker must be controlled using the State pa?ern
     */
    private void initAnimator() {
        Image imageGreen = new Image(new File("src/main/resources/bunker_green.PNG").toURI().toString(), width, height, true, true);
        Image imageYellow = new Image(new File("src/main/resources/bunker_yellow.PNG").toURI().toString(), width, height, true, true);
        Image imageRed = new Image(new File("src/main/resources/bunker_red.PNG").toURI().toString(), width, height, true, true);
        BunkerAnimation red = new BunkerAnimation("red", imageRed, null);
        BunkerAnimation yellow = new BunkerAnimation("yellow", imageYellow, red);
        BunkerAnimation green = new BunkerAnimation("green", imageGreen, yellow);
        List<Animation> animationList = new ArrayList<>();
        animationList.add(green);
        animationList.add(yellow);
        animationList.add(red);
        animator = new Animator(animationList);
        animator.setState("green");
    }

    @Override
    public void takeDamage(double amount) {
        if (isAlive()) {
            // colour change of each bunker must be controlled using the State pa?ern
            Animation state = animator.getState();
            if (state.next() != null) {
                animator.setState(state.next().getName());
            } else {
                alive = false;
            }
        }
    }

    @Override
    public double getHealth() {
        return alive ? 1 : 0;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public Image getImage() {
        return animator.getState().getCurrentFrame();
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
        return layer;
    }

    public static class BunkerBuilder {
        private Vector2D position;

        private double width;
        private double height;

        public static BunkerBuilder builder() {
            return new BunkerBuilder();
        }

        public Bunker build() {
            return new Bunker(position, width, height);
        }

        public BunkerBuilder position(Vector2D position) {
            this.position = position;
            return this;
        }

        public BunkerBuilder width(double width) {
            this.width = width;
            return this;
        }

        public BunkerBuilder height(double height) {
            this.height = height;
            return this;
        }
    }
}
