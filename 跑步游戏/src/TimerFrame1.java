
import racinggame.GameTimer;
import racinggame.TimeAnimator;
import wheelsunh.etc.DrawingPanel;
import wheelsunh.users.TextBox;

import javax.swing.*;
import java.awt.*;

class TimerFrame1 extends JFrame{
    public GameTimer gameTimer;
    public JButton startBtn, stopBtn;
    public TextBox secondTimerTextBox;

    public TimerFrame1() {
        this.setTitle("Timer");
        this.setSize(300, 120);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        wheelsunh.users.Frame._dp = new DrawingPanel();
        this.setContentPane(wheelsunh.users.Frame._dp);

        secondTimerTextBox = new TextBox(20, 20);
        TimeAnimator timeAnimator = new TimeAnimator(secondTimerTextBox);
        timeAnimator.start();

        this.init();
        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        secondTimerTextBox.paint((Graphics2D)g);

    }

    private void init() {
        // 初始化计时画板

//        JLabel timeLabel = new JLabel();
//        timeLabel.setText("0");

//        timeJPanel.add(new JLabel("Time: "));
//        timeJPanel.add(secondTimerTextBox);

        // 初始化按钮画板
        startBtn = new JButton("Start");
        stopBtn = new JButton("Stop");
        startBtn.addActionListener(e -> gameTimer.start());
        stopBtn.addActionListener(e -> gameTimer.stop());

        JPanel btnJPanel = new JPanel();
        btnJPanel.add(startBtn);
        btnJPanel.add(stopBtn);

        this.getContentPane().add(btnJPanel);
    }


    public static void main(String[] args) {
        new TimerFrame1();
    }
}
