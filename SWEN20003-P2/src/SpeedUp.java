import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class SpeedUp extends AbstractNote {
    private final static int INT_Y = 100;
    public SpeedUp(String dir, int appearanceFrame){

        super(appearanceFrame,new Image("res/noteSpeedUp.png"),INT_Y);

    }


    @Override
    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        int score = accuracy.evaluateSpecialScore(getY(), targetHeight, input.wasPressed(relevantKey), accuracy.SPEED_UP);
        if (score != Accuracy.NOT_SCORED) {
            AbstractNote.speed_factor += 1;
            deactivate();
            return score;
        }

        return score;
    }
}
