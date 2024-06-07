import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class SlowDown extends AbstractNote {
    private final static int INT_Y = 100;
    public SlowDown(String dir, int appearanceFrame){

        super(appearanceFrame,new Image("res/noteSlowDown.png"),INT_Y);

    }


    @Override
    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        int score = accuracy.evaluateSpecialScore(getY(), targetHeight, input.wasPressed(relevantKey), accuracy.SLOW_DOWN);
        if (score != Accuracy.NOT_SCORED) {
            AbstractNote.speed_factor -= 1;
            deactivate();
            return score;
        }
        return score;
    }

}
