package invaders.memento;

import invaders.engine.GameEngine;
import javafx.scene.control.Label;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-10-14
 * Time: 17:05
 */
public class UndoManage {
    // 只有一份快照
    private Snapshot snapshot;

    public void save(GameEngine gameEngine, Label timeLabel, Label scoreLabel) {
        this.snapshot = new Snapshot(gameEngine, timeLabel, scoreLabel);
    }

    public void restore() {
        if (snapshot == null) {
            return;
        }
        this.snapshot.restore();
    }
}
