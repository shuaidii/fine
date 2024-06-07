import bagel.*;
import bagel.Font;
import bagel.Image;
import bagel.Window;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Sample solution for SWEN20003 Project 1, Semester 2, 2023
 *
 * @author Stella Li
 */
public class ShadowDance extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DANCE";
    private final Image BACKGROUND_IMAGE = new Image("res/background.png");
    private final static String CSV_FILE_1 = "res/level1.csv";
    private final static String CSV_FILE_2 = "res/level2.csv";
    private final static String CSV_FILE_3 = "res/level3.csv";
    public final static String FONT_FILE = "res/FSO8BITR.TTF";
    private final static int TITLE_X = 220;
    private final static int TITLE_Y = 250;
    private final static int INS_X_OFFSET = 100;
    private final static int INS_Y_OFFSET = 190;
    private final static int SCORE_LOCATION = 35;
    private final Font TITLE_FONT = new Font(FONT_FILE, 64);
    private final Font INSTRUCTION_FONT = new Font(FONT_FILE, 24);
    private final Font SCORE_FONT = new Font(FONT_FILE, 30);
    private static final String INSTRUCTIONS = "SELECT LEVELS WITH\nNUMBER KEYS\n1  2   3";
    private static final int CLEAR_SCORE = 150;
    private static final int CLEAR_SCOR_2 = 400;
    private static final int CLEAR_SCORE_3 = 350;
    private static final String CLEAR_MESSAGE = "CLEAR!";
    private static final String TRY_AGAIN_MESSAGE = "TRY AGAIN";
    private static final String LEVEL_SELECTION_MESSAGE = "PRESS SPACE TO RETURN TO LEVEL SELECTION";
    private Accuracy accuracy;
    private ArrayList<Lane> lanes;
    private ArrayList<Enemy> enemies;
    private Guardian guardian;
    private ArrayList<Projectile> projectiles;
    private final Random random = new Random();
    private int score;
    private static int currFrame;
    private Track track = new Track("res/track1.wav");
    private boolean started = false;
    private boolean finished;
    private boolean paused;
    private int winScore;

    private int level = 0;
    public static int DOUBLE_FRAME = 0;

    public ShadowDance() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);

    }


    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDance game = new ShadowDance();
        game.run();
    }


    private void readCsv(String file) {
        lanes = new ArrayList<>();
        enemies = new ArrayList<>();
        guardian = new Guardian(800, 600);
        projectiles = new ArrayList<>();
        accuracy = new Accuracy();
        score = 0;
        currFrame = 0;
        track = new Track("res/track1.wav");
        finished = false;
        paused = false;


        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String textRead;
            while ((textRead = br.readLine()) != null) {
                String[] splitText = textRead.split(",");

                if (splitText[0].equals("Lane")) {
                    // reading lanes
                    String laneType = splitText[1];
                    int pos = Integer.parseInt(splitText[2]);
                    Lane lane = new Lane(laneType, pos);
                    lanes.add(lane);
                } else {
                    // reading notes
                    String dir = splitText[0];
                    Lane lane = null;

                    for (Lane l : lanes) {
                        if (l.getType().equals(dir)) {
                            lane = l;
                            break;
                        }
                    }


                    if (lane != null) {
                        switch (splitText[1]) {
                            case "Normal":
                                Note note = new Note(dir, Integer.parseInt(splitText[2]));
                                lane.addNote(note);
                                break;
                            case "Hold":
                                HoldNote holdNote = new HoldNote(dir, Integer.parseInt(splitText[2]));
                                lane.addNote(holdNote);
                                break;

                            case "Bomb":
                                lane.addNote(new Bomb(dir, Integer.parseInt(splitText[2])));
                                break;


                            case "DoubleAbstract":
                                lane.addNote(new DoubleAbstract(dir, Integer.parseInt(splitText[2])));
                                break;
                            case "SlowDown":
                                lane.addNote(new SlowDown(dir, Integer.parseInt(splitText[2])));
                                break;
                            case "SpeedUp":
                                lane.addNote(new SpeedUp(dir, Integer.parseInt(splitText[2])));
                                break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        if (!started) {
            // starting screen
            TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
            INSTRUCTION_FONT.drawString(INSTRUCTIONS,
                    TITLE_X + INS_X_OFFSET, TITLE_Y + INS_Y_OFFSET);

            DOUBLE_FRAME = 0;
            if (input.wasPressed(Keys.NUM_1)) {
                level = 1;
                readCsv(CSV_FILE_1);
                track = new Track("res/track1.wav");
                started = true;
                track.start();
                winScore = CLEAR_SCORE;
            } else if (input.wasPressed(Keys.NUM_2)) {
                level = 2;
                readCsv(CSV_FILE_2);
                track = new Track("res/track2.wav");
                started = true;
                track.start();
                winScore = CLEAR_SCOR_2;

            } else if (input.wasPressed(Keys.NUM_3)) {
                level = 3;
                readCsv(CSV_FILE_3);
                track = new Track("res/track3.wav");
                started = true;
                track.start();
                winScore = CLEAR_SCORE_3;

            }

        } else if (finished) {
            // end screen
            if (score >= winScore) {
                TITLE_FONT.drawString(CLEAR_MESSAGE,
                        WINDOW_WIDTH / 2 - TITLE_FONT.getWidth(CLEAR_MESSAGE) / 2,
                        WINDOW_HEIGHT / 2);
            } else {
                TITLE_FONT.drawString(TRY_AGAIN_MESSAGE,
                        WINDOW_WIDTH / 2 - TITLE_FONT.getWidth(TRY_AGAIN_MESSAGE) / 2,
                        WINDOW_HEIGHT / 2 - 80);
                INSTRUCTION_FONT.drawString(LEVEL_SELECTION_MESSAGE,
                        WINDOW_WIDTH / 2 - INSTRUCTION_FONT.getWidth(LEVEL_SELECTION_MESSAGE) / 2,
                        WINDOW_HEIGHT / 2 + 80);
                if (input.wasPressed(Keys.SPACE)) {
                    started = false;
                }

            }
        } else {
            // gameplay

            SCORE_FONT.drawString("Score " + score, SCORE_LOCATION, SCORE_LOCATION);

            if (paused) {
                if (input.wasPressed(Keys.TAB)) {
                    paused = false;
                    track.run();
                }

                for (Lane lane : lanes) {
                    lane.draw();
                }

                for (Enemy enemy : enemies) {
                    enemy.draw();
                }
                if (level == 3) {
                    guardian.draw();
                }
                for (Projectile projectileTmp : projectiles) {
                    projectileTmp.draw();
                }
            } else {
                currFrame++;
                DOUBLE_FRAME--;
                System.out.println("currFrame: " + currFrame);

                boolean clearNotes = false;
                for (Lane lane : lanes) {
                    int scoreTmp = lane.update(input, accuracy);
                    if (scoreTmp == Accuracy.BOMB_ACTIVED) {
                        clearNotes = true;
                        break;
                    }
                    score += scoreTmp;
                    if (DOUBLE_FRAME > 0 && scoreTmp > 0) {
                        score += scoreTmp;
                    }
                }
                if (clearNotes) {
                    for (Lane lane : lanes) {
                        lane.clearActiveNotes();
                    }
                }
                //画出enemy,enemy 每600frame 一次不知道咋画
                //if(currentFrame % 600 == 1)生成一个，然后加到enemy里
                //random.nextInt()范围100到900之间（x坐标），100到500之间（Y坐标）
                //loop enemy去检查每一个lane 和 enemy用lane.checkCollision,enemy.update()
                if (level == 3) {
                    if (currFrame % 600 == 0) {
                        int x = random.nextInt(800) + 100;
                        int y = random.nextInt(400) + 100;
                        Enemy enemy = new Enemy(x, y, random.nextBoolean());
                        enemies.add(enemy);
                    }
                    for (Enemy enemy : enemies) {
                        enemy.update();
                        for (Lane lane : lanes) {
                            lane.checkCollision(enemy);
                        }
                    }

                    // 处理Guardian
                    guardian.update();
                    //如果input 是 left shift, add 一个 projectile(enemies.get(0))
                    Projectile projectile = guardian.checkShoot(input, nearestEnemy(guardian));
                    if (projectile != null) {
                        projectiles.add(projectile);
                    }


                    for (Iterator<Projectile> iterator = projectiles.iterator(); iterator.hasNext(); ) {
                        Projectile projectileTmp = iterator.next();
                        projectileTmp.update();
                        if (projectileTmp.isActive()) {
                            for (Enemy enemy : enemies) {
                                if (projectileTmp.collision(enemy)) {
                                    // 射中
                                    enemy.deactivate();
                                    projectileTmp.deactivate();
                                    enemies.remove(enemy);
                                    break;
                                }
                            }
                        }
                        if (!projectileTmp.isActive()) {
                            iterator.remove();
                        }
                    }
                }

                accuracy.update();
                finished = checkFinished();
                if (input.wasPressed(Keys.TAB)) {
                    paused = true;
                    track.pause();
                }
            }
        }

    }

    private Enemy nearestEnemy(Guardian guardian) {
        if (enemies.size() == 0) {
            return null;
        }
        Enemy enemy = null;
        double distance = Double.MAX_VALUE;
        Point guardianPoint = guardian.getPoint();
        for (Enemy enemyTmp : enemies) {
            double detaX = enemyTmp.getPoint().x - guardianPoint.x;
            double detaY = enemyTmp.getPoint().y - guardianPoint.y;
            double dis = detaX * detaX + detaY * detaY;
            if (dis < distance) {
                distance = dis;
                enemy = enemyTmp;
            }
        }
        return enemy;
    }

    public static int getCurrFrame() {
        return currFrame;
    }

    private boolean checkFinished() {
        for (Lane lane : lanes) {
            if (!lane.isFinished()) {
                return false;
            }
        }
        return true;
    }
}
