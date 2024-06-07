import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class Bomb extends AbstractNote {
    private final static int INT_Y = 100;

    public Bomb(String dir, int appearanceFrame) {
        super(appearanceFrame, new Image("res/noteBomb.png"), INT_Y);
    }

    @Override
    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        int score = accuracy.evaluateSpecialScore(getY(), targetHeight, input.wasPressed(relevantKey), accuracy.BOMB);
        if (score == Accuracy.SPECIAL_SCORE) {
            deactivate();
            return Accuracy.BOMB_ACTIVED;
        }

        return Accuracy.NOT_SCORED;
    }
}

