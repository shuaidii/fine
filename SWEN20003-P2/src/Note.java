import bagel.*;

/**
 * Class for normal notes
 */
public class Note extends AbstractNote {
    private final static int INT_Y = 100;
    private final String type = null;

    public Note(String dir, int appearanceFrame) {
        super(appearanceFrame, new Image("res/Note" + dir + ".png"), INT_Y);

    }







    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        if (isActive()) {
            // evaluate accuracy of the key press
            int score = accuracy.evaluateScore(getY(), targetHeight, input.wasPressed(relevantKey));

            if (score != Accuracy.NOT_SCORED) {
                deactivate();
                return score;
            }

        }

        return 0;
    }


    //这个需要给其他note也弄，但不太会
    public String getType(){
        return type;
    }
}
