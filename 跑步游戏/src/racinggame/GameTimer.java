package racinggame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameTimer {
    /**
     * show time JLabel in the JPanel of JFrame
     */
    JLabel timeLabel;
    /**
     * play time in second
     */
    int second = 0;

    Timer timer;

    class GameTimerAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            second++;
            timeLabel.setText(String.valueOf(second));
        }
    }

    public GameTimer(JLabel timeLabel) {
        this.timeLabel = timeLabel;
        timer = new Timer(1000, new GameTimerAction());
    }

    /**
     * start time when starting to play
     */
    public void start() {
        second = 0;
        timeLabel.setText(String.valueOf(second));
        timer.restart();
    }

    /**
     * stop time when play end
     */
    public void stop() {
        timer.stop();
    }

    public int getSecond() {
        return second;
    }


}
