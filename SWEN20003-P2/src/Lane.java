import bagel.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class for the lanes which notes fall down
 */
public class Lane {
    private static final int HEIGHT = 384;
    private static final int TARGET_HEIGHT = 657;
    private final String type;
    private final Image image;

    private Keys relevantKey;
    private final int location;

    private final ArrayList<AbstractNote> abstractNotes = new ArrayList<>();


    public Lane(String dir, int location) {
        this.type = dir;
        this.location = location;
        image = new Image("res/lane" + dir + ".png");
        switch (dir) {
            case "Left":
                relevantKey = Keys.LEFT;
                break;
            case "Right":
                relevantKey = Keys.RIGHT;
                break;
            case "Up":
                relevantKey = Keys.UP;
                break;
            case "Down":
                relevantKey = Keys.DOWN;
                break;

            case "Special":
                relevantKey = Keys.SPACE;
                break;
        }
    }

    public String getType() {
        return type;
    }

    /**
     * updates all the notes in the lane
     */
    public int update(Input input, Accuracy accuracy) {
        draw();

        for (AbstractNote note : abstractNotes) {
            note.update();
        }

        if (!abstractNotes.isEmpty()) {
            AbstractNote note = abstractNotes.get(0);
            int score = note.checkScore(input, accuracy, TARGET_HEIGHT, relevantKey);
            if (note.isCompleted()) {
                abstractNotes.remove(note);
            } else if (note.getY() >= 768 + note.getImage().getHeight() / 2) {
                note.deactivate();
                abstractNotes.remove(note);
            }

            if (score == Accuracy.BOMB_ACTIVED) {
                // 消除abstractnote 中active的note(不能用for)
                return score;
            } else {
                return score;
            }

        }

        return Accuracy.NOT_SCORED;
    }

    public void addNote(AbstractNote n) {
        abstractNotes.add(n);
    }

    /**
     * Finished when all the notes have been pressed or missed
     */
    public boolean isFinished() {
        return abstractNotes.isEmpty();
    }

    /**
     * draws the lane and the notes
     */
    public void draw() {
        image.draw(location, HEIGHT);
        for (AbstractNote note : abstractNotes) {
            note.draw(location);
        }
    }

    public void checkCollision(Enemy enemy) {
        //检查enemy 是否与note 碰撞
        // loop 每个note看是否撞上
        //如果撞上普通note或者holdnote，就remove这个note
        // collision 范围是104
        for (Iterator<AbstractNote> iterator = abstractNotes.iterator(); iterator.hasNext(); ) {
            AbstractNote note = iterator.next();
            double detaX = enemy.getPoint().x - location;
            double detaY = enemy.getPoint().y - note.getY();
            double distance = Math.sqrt(detaX * detaX + detaY * detaY);
            if (distance <= 104) {
                iterator.remove();
            }
        }
    }

    public void clearActiveNotes() {
        abstractNotes.removeIf(AbstractNote::isActive);
    }
}
