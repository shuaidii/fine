package invaders.rendering;

import invaders.physics.Collider;
import invaders.physics.Vector2D;
import javafx.scene.image.Image;

/**
 * Represents something that can be rendered
 */
public interface Renderable extends Collider {

    public Image getImage();

    public double getWidth();

    public double getHeight();

    public Vector2D getPosition();

    public Renderable.Layer getLayer();

    /**
     * The set of available layers
     */
    public static enum Layer {
        BACKGROUND, FOREGROUND, EFFECT
    }

//    /**
//     * collide with other Renderable
//     *
//     * @param other Renderable
//     * @return collide or not
//     */
//    default boolean isColliding(Renderable other) {
//        double x0 = Math.abs(getPosition().getX() - other.getPosition().getX());
//        double y0 = Math.abs(getPosition().getY() - other.getPosition().getY());
//        double width1 = getWidth(), height1 = getHeight();
//        double width2 = other.getWidth(), height2 = other.getHeight();
//        return (width1 - width2) / 2 < x0 && x0 < (width1 + width2) / 2 &&
//                (height1 - height2) / 2 < y0 && y0 < (height1 + height2) / 2;
//    }


}
