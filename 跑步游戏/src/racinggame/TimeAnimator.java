package racinggame;

import wheelsunh.users.AnimationTimer;
import wheelsunh.users.Animator;
import wheelsunh.users.TextBox;

public class TimeAnimator implements Animator {
    private TextBox secondTimerTextBox;
    private AnimationTimer timer;
    private int second = 0;

    public TimeAnimator(TextBox secondTimerTextBox) {
        this.secondTimerTextBox = secondTimerTextBox;
        this.timer = new AnimationTimer(1000, this);
    }

    @Override
    public void animate() {
        second++;
        secondTimerTextBox.setText("Second: " + second);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void reset() {
        second = 0;
        timer.restart();
    }
}
