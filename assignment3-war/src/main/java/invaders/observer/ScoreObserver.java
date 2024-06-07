package invaders.observer;

import javafx.scene.control.Label;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-10-12
 * Time: 23:19
 */
public class ScoreObserver implements Observer {
    private Label scoreLabel;

    public ScoreObserver(Label scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    @Override
    public void update(int incrScore) {
        int score = Integer.parseInt(scoreLabel.getText().split(" ")[1]);
        score += incrScore;
        scoreLabel.setText("Score " + score);
    }
}
