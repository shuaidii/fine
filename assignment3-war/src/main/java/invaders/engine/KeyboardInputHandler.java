package invaders.engine;

import invaders.adpter.Cheat;
import invaders.adpter.CheatAdapter;
import invaders.memento.UndoManage;
import invaders.state.GameState;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class KeyboardInputHandler {
    private final GameEngine model;
    private boolean left = false;
    private boolean right = false;
    private Set<KeyCode> pressedKeys = new HashSet<>();

    private Map<String, MediaPlayer> sounds = new HashMap<>();
    private Cheat cheat;
    private Label timeLabel;
    private Label scoreLabel;
    private UndoManage undoManage;

    KeyboardInputHandler(GameEngine model, Label timeLabel, Label scoreLabel) {
        this.model = model;
        this.cheat = new CheatAdapter(model);
        this.timeLabel = timeLabel;
        this.scoreLabel = scoreLabel;
        this.undoManage = new UndoManage();

        // TODO (longGoneUser): Is there a better place for this code?
        URL mediaUrl = getClass().getResource("/shoot.wav");
        String jumpURL = mediaUrl.toExternalForm();

        Media sound = new Media(jumpURL);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        sounds.put("shoot", mediaPlayer);
    }

    void handlePressed(KeyEvent keyEvent) {
        if (pressedKeys.contains(keyEvent.getCode())) {
            return;
        }
        pressedKeys.add(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.SPACE)) {
            if (model.shootPressed()) {
                MediaPlayer shoot = sounds.get("shoot");
                shoot.stop();
                shoot.play();
            }
        }

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = true;
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = true;
        }

        if (left) {
            model.leftPressed();
        }

        if (right) {
            model.rightPressed();
        }
    }

    void handleReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = false;
            model.leftReleased();
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            model.rightReleased();
            right = false;
        }
        // 加载不同难度的游戏
        if (keyEvent.getCode().equals(KeyCode.DIGIT1) && GameWindow.getGameState().equals(GameState.START)) {
            model.loadGame("src/main/resources/config_easy.json");
            GameWindow.setGameState(GameState.PLAYING);
        }
        if (keyEvent.getCode().equals(KeyCode.DIGIT2) && GameWindow.getGameState().equals(GameState.START)) {
            model.loadGame("src/main/resources/config_medium.json");
            GameWindow.setGameState(GameState.PLAYING);
        }
        if (keyEvent.getCode().equals(KeyCode.DIGIT3) && GameWindow.getGameState().equals(GameState.START)) {
            model.loadGame("src/main/resources/config_hard.json");
            GameWindow.setGameState(GameState.PLAYING);
        }

        // Undo
        if (keyEvent.getCode().equals(KeyCode.S) && GameWindow.getGameState() == GameState.PLAYING) {
            undoManage.save(model, timeLabel, scoreLabel);
        }
        if (keyEvent.getCode().equals(KeyCode.R)) {
            undoManage.restore();
        }

        // cheat
        cheat.execute(keyEvent.getCode());
    }
}
