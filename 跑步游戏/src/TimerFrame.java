
import racinggame.GameTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TimerFrame {
    public GameTimer gameTimer;
    public JButton startBtn, stopBtn;

    public TimerFrame() {
        JFrame timerFrame = new JFrame();
        timerFrame.setTitle("Timer");
        timerFrame.setSize(300, 120);
        timerFrame.setLocationRelativeTo(null);
        timerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        timerFrame.add(init());
        timerFrame.setVisible(true);
    }

    private JPanel init() {
        // 初始化计时画板
        JLabel timeLabel = new JLabel();
        timeLabel.setText("0");
        gameTimer = new GameTimer(timeLabel);

        JPanel timeJPanel = new JPanel();
        timeJPanel.add(new JLabel("Time: "));
        timeJPanel.add(timeLabel);

        // 初始化按钮画板
        startBtn = new JButton("Start");
        stopBtn = new JButton("Stop");
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameTimer.start();
            }
        });
        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameTimer.stop();
            }
        });

        JPanel btnJPanel = new JPanel();
        btnJPanel.add(startBtn);
        btnJPanel.add(stopBtn);

        // 总画板
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(timeJPanel, BorderLayout.CENTER);
        jPanel.add(btnJPanel, BorderLayout.SOUTH);
        return jPanel;
    }


    public static void main(String[] args) {
        new TimerFrame();
    }
}
