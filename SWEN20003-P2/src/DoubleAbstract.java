import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class DoubleAbstract extends AbstractNote {
    private final static int INT_Y = 100;

    public DoubleAbstract(String dir, int appearanceFrame) {

        super(appearanceFrame, new Image("res/note2x.PNG"), INT_Y);

    }


    @Override
    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        // 在accuracy里面添加东西实现两倍分数
        // 持续480 frame
        int score = accuracy.evaluateSpecialScore(getY(), targetHeight, input.wasPressed(relevantKey), accuracy.DOUBLE_SCORE);
        if (score != Accuracy.NOT_SCORED) {
            deactivate();
            ShadowDance.DOUBLE_FRAME = 480;
        }
        return 0;
    }
}
