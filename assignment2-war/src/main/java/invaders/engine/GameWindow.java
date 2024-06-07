package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import invaders.logic.Damagable;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameWindow {
    public static int width;
    public static int height;
    private Scene scene;
    private Pane pane;
    private GameEngine gameEngine;
    private List<EntityView> entityViews;
    private Renderable background;

    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    private static final double VIEWPORT_MARGIN = 280.0;

    public GameWindow(GameEngine gameEngine, int width, int height){
        this.width = width;
        this.height = height;
        this.gameEngine = gameEngine;
        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.background = new SpaceBackground(gameEngine, pane);

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(this.gameEngine);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        entityViews = new ArrayList<EntityView>();

    }

    public void run() {
         Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> this.draw()));

         timeline.setCycleCount(Timeline.INDEFINITE);
         timeline.play();
    }

    private void draw(){
        if (gameEngine.isEnd()) {
            return;
        }

        gameEngine.update();
        if (gameEngine.isEnd()) {
            System.out.println("Game End");
            return;
        }

        List<Renderable> renderables = gameEngine.getRenderables();
        for (Renderable entity : renderables) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    if (entity instanceof Damagable) {
                        if (!((Damagable) entity).isAlive()) {
                            view.markForDelete();
                        }
                    }
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
                gameEngine.removeRenderable(entityView.getEntity());
            }
        }
        entityViews.removeIf(EntityView::isMarkedForDelete);
    }

    public Scene getScene() {
        return scene;
    }
}
