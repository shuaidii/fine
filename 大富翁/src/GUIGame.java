import meta.BuildingSquare;
import meta.Player;
import meta.Square;
import meta.StartSquare;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GUIGame extends Game {
    private static final int WIDTH = 880 + 14;
    private static final int HEIGHT = 880 + 18;
    private static final Font FONT_24 = new Font("YaHei", Font.BOLD, 24);
    private static final Font FONT_18 = new Font("YaHei", Font.BOLD, 18);

    private Map<Integer, JPanel> squarePanelMap = new HashMap<>();
    private Map<Player, JPanel> playerJPanelMap = new HashMap<>();
    private JLabel notifyLabel;
    private JLabel dieLabel;

    private JFrame jFrame;

    public GUIGame() {
        this(false);
    }

    public GUIGame(boolean cheatingMode) {
        super(cheatingMode);

        jFrame = new JFrame();
        jFrame.setTitle("HOTELS");
        jFrame.setSize(WIDTH, HEIGHT + 20);

        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(initJPanel());
        jFrame.setVisible(true);
    }

    @Override
    public void start() {
        drawAll();

        randomStartPlayer();
        while (true) {
            int step = playerMove();

            Player winner = getWinner();
            if (winner != null) {
                notifyLabel.setText("Game end, Player " + winner.getName() + " wins");
                break;
            }

            drawSquareJPanel(currentPlayer.getCurrentSquare());
            freshPlayerJPanel();
            sleep(2000);

            this.currentPlayer = nextPlayer();
            freshPlayerJPanel();
        }
    }

    @Override
    public int playerMove() {
        notifyLabel.setText("Player " + currentPlayer.getName() + " start to play");
        int step = 1;
        if (cheatingMode) {
            while (true) {
                String res = JOptionPane.showInputDialog(null, "Input a die number from 1-12: ",
                        "Die Number", JOptionPane.INFORMATION_MESSAGE);
                try {
                    step = Integer.parseInt(res);
                    if (step >= 1 && step <= 12) {
                        break;
                    }
                } catch (Exception e) {
                }
            }
        } else {
            step = die(12);
        }

        notifyLabel.setText("Move step: " + step);
        for (int i = 1; i <= step; i++) {
            Square currentSquare = currentPlayer.getCurrentSquare();
            int nextSquareIndex = ((1 + currentSquare.getIndex()) % board.getSquareCount());
            Square nextSquare = board.getSquare(nextSquareIndex);
            currentPlayer.setCurrentSquare(nextSquare);
            drawSquareJPanel(currentSquare);
            drawSquareJPanel(nextSquare);
            sleep(200);
        }
        processOnSquare(currentPlayer.getCurrentSquare());
        return step;
    }

    @Override
    public void processOnSquare(Square square) {
        if (!(square instanceof BuildingSquare)) {
            return;
        }

        BuildingSquare buildingSquare = (BuildingSquare) square;
        // pay for overnight fee
        if (buildingSquare.getOwner() != null && !buildingSquare.getOwner().equals(currentPlayer)) {
            int overnightFee = buildingSquare.getOvernightFee();
            //  the fee is doubled if the owner owns both of the other two hotels in the letter group eg A3 and A2
            //  as well as A1 and is halved if the guest owns either or both of the other two hotels in the letter group.
            if (buildingSquare.getOwner().getBuildingCount(buildingSquare.getBuildingType()) == 3) {
                overnightFee *= 2;
            } else if (currentPlayer.getBuildingCount(buildingSquare.getBuildingType()) == 2) {
                overnightFee /= 2;
            }
            notifyLabel.setText("Must pay $" + overnightFee + " for overnight fee.");
            currentPlayer.decrAmount(overnightFee);
            buildingSquare.getOwner().incrAmount(overnightFee);
            return;
        }

        // buy the building
        if (buildingSquare.getOwner() == null) {
            if (buildingSquare.getSellPrice() > currentPlayer.getRemainAmount()) {
                notifyLabel.setText("Sorry, you has not enough money to have chance to buy this building with $" + buildingSquare.getSellPrice());
            } else {
                int option = JOptionPane.showConfirmDialog(null, "Buy this building with $"
                        + buildingSquare.getSellPrice() + "?", "Buy Building", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    currentPlayer.buyBuilding(buildingSquare);
                    notifyLabel.setText("Buy successful");
                    freshPlayerJPanel();
                    drawSquareJPanel(buildingSquare);
                    sleep(100);
                }
            }
        }

        // increase the building star rating
        if (currentPlayer.equals(buildingSquare.getOwner())) {
            while (buildingSquare.canIncrStarRating()) {
                int option = JOptionPane.showConfirmDialog(null, "Increase the star rating with $"
                        + buildingSquare.incrStarRatingCost() + "?", "Increase Star Rating", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    buildingSquare.incrStarRating();
                    notifyLabel.setText("Increase Star Rating successful");
                    freshPlayerJPanel();
                    drawSquareJPanel(buildingSquare);
                    sleep(200);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void randomStartPlayer() {
        if (cheatingMode) {
            String[] ss = {"Player 1", "Player 2"};
            int res = JOptionPane.showOptionDialog(null, "Select first player:", "Select",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, ss, "Player 1");
            if (res == 0) {
                currentPlayer = playerList.get(0);
            } else {
                currentPlayer = playerList.get(1);
            }
            notifyLabel.setText("Select first player: " + (res + 1));
        } else {

            notifyLabel.setText("Random select first player: ");
            int number = die(2);
            notifyLabel.setText("Random select first player: " + number);
            currentPlayer = playerList.get(number - 1);
            sleep(2000);
            currentPlayer = playerList.get(0);
        }
        freshPlayerJPanel();
    }

    private void drawAll() {
        for (int i = 0; i < 40; i++) {
            drawSquareJPanel(i);
        }
        freshPlayerJPanel();
    }

    private void freshPlayerJPanel() {
        for (Player player : playerJPanelMap.keySet()) {
            JPanel jPanel = playerJPanelMap.get(player);
            JLabel title = new JLabel("Player " + player.getName(), JLabel.CENTER);
            try {
                jPanel.removeAll();
            } catch (Exception e) {
            }
            title.setFont(FONT_24);
            jPanel.add(title, BorderLayout.CENTER);

            JLabel amount = new JLabel("$" + player.getRemainAmount(), JLabel.CENTER);
            amount.setFont(FONT_18);
            jPanel.add(amount, BorderLayout.SOUTH);

            if (currentPlayer == player) {
                jPanel.setBackground(player.getColor());
            } else {
                jPanel.setBackground(null);
            }

            jPanel.revalidate();
        }
    }

    /**
     * display square in JPanel
     *
     * @param square
     */
    private void drawSquareJPanel(Square square) {
        drawSquareJPanel(square.getIndex());
    }

    /**
     * display square in JPanel
     *
     * @param squareIndex
     */
    private void drawSquareJPanel(int squareIndex) {
        Map<Square, Player> standPlayerSquareMap = new HashMap<>();
        for (Player player : playerList) {
            standPlayerSquareMap.put(player.getCurrentSquare(), player);
        }

        JPanel jPanel = squarePanelMap.get(squareIndex);
        Square square = board.getSquare(squareIndex);
        jPanel.setBackground(Color.WHITE);
        jPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        try {
            jPanel.removeAll();
        } catch (Exception e) {
        }
        if (square instanceof StartSquare) {
            JLabel jLabel = new JLabel("GO", JLabel.CENTER);
            jLabel.setFont(FONT_24);
            jLabel.setForeground(Color.magenta);
            jPanel.add(jLabel, BorderLayout.CENTER);
        }

        // 边框设成买家颜色
        if (square.getOwner() != null) {
            Color color = square.getOwner().getColor();
            jPanel.setBorder(BorderFactory.createLineBorder(color, 3));
        }

        // 玩家站立的方块，背景颜色
        if (standPlayerSquareMap.containsKey(square)) {
            if (standPlayerSquareMap.size() == 1) {
                // 都站在一个方块上，绿色
                jPanel.setBackground(Color.GREEN);
            } else {
                // 填充成玩家的颜色
                jPanel.setBackground(standPlayerSquareMap.get(square).getColor());
            }
        }

        if (square instanceof BuildingSquare) {
            BuildingSquare buildingSquare = (BuildingSquare) square;
            JLabel titleLabel = new JLabel(buildingSquare.getName(), JLabel.CENTER);
            titleLabel.setFont(FONT_18);
            jPanel.add(titleLabel, BorderLayout.NORTH);

            String star = "Star: ";
            for (int i = 0; i < buildingSquare.getStarRating(); i++) {
                star += "*";
            }
            if (buildingSquare.getStarRating() == 0) {
                star += "-";
            }
            jPanel.add(new JLabel(star, JLabel.CENTER), BorderLayout.CENTER);

            int price = buildingSquare.getSellPrice();
            if (buildingSquare.getOwner() != null) {
                price = buildingSquare.getOvernightFee();
            }
            jPanel.add(new JLabel("$" + price, JLabel.CENTER), BorderLayout.SOUTH);
        }
        jPanel.revalidate();

    }

    /**
     * throwing a twelve-sided die
     *
     * @return 1-12
     */
    public int die(int max) {
        int number = 0;
        for (int i = 0; i < 50; i++) {
            number = RANDOM.nextInt(max) + 1;
            dieLabel.setText("Die: " + number);
            sleep(50);
        }
        return number;
    }


    private JPanel initJPanel() {
        JPanel boardJPanel = new JPanel();
        boardJPanel.setSize(WIDTH, HEIGHT);
        boardJPanel.setLayout(null);

        initSquareJPanel(boardJPanel);
        initPlayerJPanel(boardJPanel);

        dieLabel = new JLabel("Die: 0");
        dieLabel.setBorder(BorderFactory.createLineBorder(Color.green, 2));
        dieLabel.setFont(FONT_24);
        dieLabel.setBounds(400, 450, 100, 50);
        boardJPanel.add(dieLabel);

        notifyLabel = new JLabel("Notify", JLabel.CENTER);
        notifyLabel.setFont(FONT_18);
        notifyLabel.setBorder(BorderFactory.createLineBorder(Color.green, 2));
        notifyLabel.setBounds(200, 600, 500, 100);
        boardJPanel.add(notifyLabel);
        return boardJPanel;
    }

    private void initPlayerJPanel(JPanel boardJPanel) {
        Player player1 = playerList.get(0);
        player1.setColor(Color.RED);
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.setBorder(BorderFactory.createLineBorder(player1.getColor(), 2));
        jPanel1.setBounds(120, 200, 150, 100);
        boardJPanel.add(jPanel1);
        playerJPanelMap.put(player1, jPanel1);

        Player player2 = playerList.get(1);
        player2.setColor(Color.CYAN);
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new BorderLayout());
        jPanel2.setBorder(BorderFactory.createLineBorder(player2.getColor(), 2));
        jPanel2.setBounds(610, 200, 150, 100);
        playerJPanelMap.put(player2, jPanel2);

        boardJPanel.add(jPanel2);
    }

    private void initSquareJPanel(JPanel boardJPanel) {
        for (int i = 0; i < 40; i++) {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(new Label(i + "", JLabel.CENTER), BorderLayout.CENTER);
            squarePanelMap.put(i, jPanel);
        }

        int startX = 0;
        int startY = 0;
        int width = 80;
        for (int i = 20; i < 30; i++) {
            JPanel jPanel = squarePanelMap.get(i);
            jPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            jPanel.setPreferredSize(new Dimension(width, width));
            jPanel.setBounds(startX + width * (i - 20), startY, width, width);
            boardJPanel.add(jPanel);
        }

        startX = 800;
        startY = 0;
        for (int i = 30; i < 40; i++) {
            JPanel jPanel = squarePanelMap.get(i);
            jPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            jPanel.setPreferredSize(new Dimension(width, width));
            jPanel.setBounds(startX, startY + width * (i - 30), width, width);
            boardJPanel.add(jPanel);
        }

        startX = 0;
        startY = 800;
        for (int i = 0; i < 10; i++) {
            JPanel jPanel = squarePanelMap.get(i);
            jPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            jPanel.setPreferredSize(new Dimension(width, width));
            jPanel.setBounds(startX + width * (10 - i), startY, width, width);
            boardJPanel.add(jPanel);
        }

        startX = 0;
        startY = 0;
        for (int i = 10; i < 20; i++) {
            JPanel jPanel = squarePanelMap.get(i);
            jPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            jPanel.setPreferredSize(new Dimension(width, width));
            jPanel.setBounds(startX, startY + width * (20 - i), width, width);
            boardJPanel.add(jPanel);
        }

    }


}
