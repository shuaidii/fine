package invaders.engine;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import invaders.observer.ScoreObserver;
import invaders.state.GameState;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameWindow {
    private final int width;
    private final int height;
    private Scene scene;
    private Pane pane;
    private GameEngine model;
    private List<EntityView> entityViews = new ArrayList<EntityView>();
    private Renderable background;

    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    // private static final double VIEWPORT_MARGIN = 280.0;

    private static GameState gameState = GameState.START;
    private List<Label> descLabels = new ArrayList<>();
    private Label timeLabel;
    private Label scoreLabel;
    private Label resultLabel;
    private boolean init = false;

    public GameWindow(GameEngine model) {
        this.model = model;
        this.width = model.getGameWidth();
        this.height = model.getGameHeight();
        this.timeLabel = new Label("Time 0:00");
        this.scoreLabel = new Label("Score 0");

        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.background = new SpaceBackground(model, pane);

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(this.model, timeLabel, scoreLabel);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

    }

    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void draw() {
        if (gameState.equals(GameState.START)) {
            selectDifficultyLevel();
        } else if (gameState.equals(GameState.STOP)) {
            showResult();
        } else if (gameState.equals(GameState.PLAYING)) {
            if (resultLabel != null) {
                pane.getChildren().remove(resultLabel);
                resultLabel = null;
            }
            pane.getChildren().removeAll(descLabels);
            if (!init) {
                initRecord();
            } else if (model.checkGameEnd()) {
                gameState = GameState.STOP;
                return;
            }

            model.update();

            List<Renderable> renderables = model.getRenderables();
            for (Renderable entity : renderables) {
                boolean notFound = true;
                for (EntityView view : entityViews) {
                    if (view.matchesEntity(entity)) {
                        notFound = false;
                        view.update(xViewportOffset, yViewportOffset);
                        break;
                    }
                }
                if (notFound) {
                    EntityView entityView = new EntityViewImpl(entity);
                    entityViews.add(entityView);
                    pane.getChildren().add(entityView.getNode());
                }
            }

            for (Renderable entity : renderables) {
                if (!entity.isAlive()) {
                    for (EntityView entityView : entityViews) {
                        if (entityView.matchesEntity(entity)) {
                            entityView.markForDelete();
                        }
                    }
                }
            }

            for (EntityView entityView : entityViews) {
                boolean notFound = true;
                // undo时，删除原来元素
                for (Renderable entity : renderables) {
                    if (entityView.matchesEntity(entity)) {
                        notFound = false;
                        break;
                    }
                }
                if (notFound) {
                    entityView.markForDelete();
                }
                if (entityView.isMarkedForDelete()) {
                    pane.getChildren().remove(entityView.getNode());
                }
            }


            model.getGameObjects().removeAll(model.getPendingToRemoveGameObject());
            model.getGameObjects().addAll(model.getPendingToAddGameObject());
            model.getRenderables().removeAll(model.getPendingToRemoveRenderable());
            model.getRenderables().addAll(model.getPendingToAddRenderable());

            model.getPendingToAddGameObject().clear();
            model.getPendingToRemoveGameObject().clear();
            model.getPendingToAddRenderable().clear();
            model.getPendingToRemoveRenderable().clear();

            entityViews.removeIf(EntityView::isMarkedForDelete);
        }
    }

    private void showResult() {
        if (resultLabel != null) {
            return;
        }
        if (model.getPlayer().isAlive()) {
            resultLabel = new Label("Game Win");
        } else {
            resultLabel = new Label("Game Lost");
        }
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        resultLabel.setTextFill(Color.WHITE);
        resultLabel.setLayoutX(250);
        resultLabel.setLayoutY(400);
        pane.getChildren().add(resultLabel);
    }


    private void selectDifficultyLevel() {
        if (descLabels.size() > 0) {
            return;
        }
        Label selectLabel = new Label("Select Difficulty Level");
        selectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        selectLabel.setTextFill(Color.WHITE);
        selectLabel.setLayoutX(200);
        selectLabel.setLayoutY(300);
        pane.getChildren().add(selectLabel);
        descLabels.add(selectLabel);

        Font font = Font.font("Arial", FontWeight.BOLD, 20);
        Label easyLabel = new Label("1. Easy");
        easyLabel.setFont(font);
        easyLabel.setTextFill(Color.WHITE);
        easyLabel.setLayoutX(220);
        easyLabel.setLayoutY(350);
        pane.getChildren().add(easyLabel);
        descLabels.add(easyLabel);

        Label middleLabel = new Label("2. Medium");
        middleLabel.setFont(font);
        middleLabel.setTextFill(Color.WHITE);
        middleLabel.setLayoutX(220);
        middleLabel.setLayoutY(400);
        pane.getChildren().add(middleLabel);
        descLabels.add(middleLabel);

        Label hardLabel = new Label("3. Hard");
        hardLabel.setFont(font);
        hardLabel.setTextFill(Color.WHITE);
        hardLabel.setLayoutX(220);
        hardLabel.setLayoutY(450);
        pane.getChildren().add(hardLabel);
        descLabels.add(hardLabel);

    }

    private Label initRecord() {
        init = true;

        Font font = Font.font("Arial", FontWeight.BOLD, 20);
        timeLabel.setFont(font);
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setLayoutX(20);
        timeLabel.setLayoutY(20);
        pane.getChildren().add(timeLabel);

        scoreLabel.setFont(font);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setLayoutX(500);
        scoreLabel.setLayoutY(20);
        pane.getChildren().add(scoreLabel);
        model.registerObserver(new ScoreObserver(scoreLabel));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    if (gameState != GameState.PLAYING) {
                        return;
                    }
                    String timeStr = timeLabel.getText().split(" ")[1];
                    int second = Integer.parseInt(timeStr.split(":")[0]) * 60 + Integer.parseInt(timeStr.split(":")[1]);
                    second++;
                    updateTime(second);
                });
            }
        }, 0, 1000);

        return timeLabel;
    }

    private void updateTime(int second) {
        int minute = second / 60;
        second = second % 60;
        String msg = "Time " + minute + ":";
        if (second < 10) {
            msg += "0";
        }
        msg += second;
        timeLabel.setText(msg);
        System.out.println("Time: " + msg);
    }

    public Scene getScene() {
        return scene;
    }

    public static GameState getGameState() {
        return gameState;
    }

    public static void setGameState(GameState gameState) {
        GameWindow.gameState = gameState;
    }
}
