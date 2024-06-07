package invaders.memento;

import invaders.engine.GameEngine;
import invaders.engine.GameWindow;
import invaders.entities.Player;
import invaders.gameobject.GameObject;
import invaders.rendering.Renderable;
import invaders.state.GameState;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-10-14
 * Time: 16:56
 */
public class Snapshot {
    private GameEngine gameEngine;
    private Label timeLabel;
    private Label scoreLabel;

    private String score;
    private String time;
    private List<Renderable> renderables;

    public Snapshot(GameEngine gameEngine, Label timeLabel, Label scoreLabel) {
        this.gameEngine = gameEngine;
        this.timeLabel = timeLabel;
        this.scoreLabel = scoreLabel;

        this.store();
    }

    private void store() {
        this.time = timeLabel.getText();
        this.score = scoreLabel.getText();
        try {
            this.renderables = new ArrayList<>();
            for (Renderable renderable : gameEngine.getRenderables()) {
                renderables.add(renderable.clone());
            }
            System.out.println("Undo save ...");
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void restore() {
        timeLabel.setText(time);
        scoreLabel.setText(score);
        GameWindow.setGameState(GameState.PLAYING);
        try {
            gameEngine.getRenderables().clear();
            gameEngine.getGameObjects().clear();
            for (Renderable renderable : renderables) {
                Renderable copy = renderable.clone();
                if (copy instanceof Player) {
                    gameEngine.setPlayer((Player)copy);
                }
                gameEngine.getRenderables().add(copy);
                if (copy instanceof GameObject) {
                    gameEngine.getGameObjects().add((GameObject) copy);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Undo restore ...");
    }


}
