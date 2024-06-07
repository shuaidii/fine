package invaders.engine;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import invaders.GameObject;
import invaders.entities.*;
import invaders.physics.Moveable;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine {

    private static final Random RANDOM = new Random();

    private List<GameObject> gameobjects;
    private List<Renderable> renderables;
    private Player player;

    private boolean left;
    private boolean right;

    private int sizeX = 640;
    private int sizeY = 400;

    private boolean end = false;

    public GameEngine(String config) {
        // read the config here
        gameobjects = new ArrayList<GameObject>();
        renderables = new ArrayList<Renderable>();
        player = new Player(new Vector2D(200, 380));
        renderables.add(player);

        initDataWithConfigFile(config);
    }

    private void initDataWithConfigFile(String config) {
        try {
            URL url = getClass().getResource(config);
            JSONObject jsonObject = (JSONObject) JSONValue.parse(new InputStreamReader(url.openStream()));
            initGameSize(getJSON(jsonObject, "Game"));
            initPlayer(getJSON(jsonObject, "Player"));
            initBunkers(getArray(jsonObject, "Bunkers"));
            initEnemies(getArray(jsonObject, "Enemies"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isEnd() {
        return end;
    }

    /**
     * Updates the game/simulation
     */
    public void update() {
        movePlayer();

        for (GameObject go : gameobjects) {
            go.update();
        }

        // ensure that renderable foreground objects don't go off-screen
        for (Renderable ro : renderables) {
            if (!ro.getLayer().equals(Renderable.Layer.FOREGROUND)) {
                continue;
            }
            if (ro instanceof Player || ro instanceof Enemy) {
                if (ro.getPosition().getX() + ro.getWidth() >= 640) {
                    ro.getPosition().setX(639 - ro.getWidth());
                }

                if (ro.getPosition().getX() <= 0) {
                    ro.getPosition().setX(1);
                }

                if (ro.getPosition().getY() + ro.getHeight() >= 400) {
                    ro.getPosition().setY(399 - ro.getHeight());
                }

                if (ro.getPosition().getY() <= 0) {
                    ro.getPosition().setY(1);
                }
            }

            if (ro instanceof Projectile) {
                //炮弹出边界
                if (ro.getPosition().getY() - ro.getHeight() >= 400 || ro.getPosition().getY() + ro.getHeight() <= 0) {
//                    System.out.println("ro.getPosition().getY()=" + ro.getPosition().getY());
//                    System.out.println("ro.getHeight()=" + ro.getHeight());
                    ((Projectile) ro).setAlive(false);
                }
            }

            if (ro instanceof Moveable) {
                ((Moveable) ro).up();
                ((Moveable) ro).left();
                ((Moveable) ro).right();
                ((Moveable) ro).down();
            }
        }

        checkCollide();

        enemyShoot();

        checkGame();
    }

    private void checkCollide() {
        List<Projectile> projectiles = renderables.stream().filter(renderable -> renderable instanceof Projectile)
                .map(renderable -> (Projectile) renderable).collect(Collectors.toList());
        List<Enemy> enemyList = renderables.stream().filter(renderable -> renderable instanceof Enemy)
                .map(renderable -> (Enemy) renderable).collect(Collectors.toList());
        List<Bunker> bunkerList = renderables.stream().filter(renderable -> renderable instanceof Bunker)
                .map(renderable -> (Bunker) renderable).collect(Collectors.toList());

        List<Projectile> playerProjectiles = new ArrayList<>();
        for (Projectile projectile : projectiles) {
            if (!projectile.isAlive()) {
                break;
            }
            if (!projectile.isPlayer() && projectile.isColliding(player)) {
                // 敌人子弹射中玩家
                player.takeDamage(1);
                System.out.println("Warning: Player got one damage!!!");
                projectile.setAlive(false);
                break;
            } else if (projectile.isPlayer()) {
                // 玩家子弹射中敌人
                playerProjectiles.add(projectile);
                for (Enemy enemy : enemyList) {
                    if (enemy.isAlive() && projectile.isColliding(enemy)) {
                        enemy.takeDamage(1);
                        projectile.setAlive(false);
                        Enemy.speedRate += 0.1; // 敌人移动加速
                        break;
                    }
                }
            }

            if (projectile.isAlive()) {
                // 子弹射中掩体
                for (Bunker bunker : bunkerList) {
                    if (bunker.isAlive() && projectile.isColliding(bunker)) {
                        bunker.takeDamage(1);
                        projectile.setAlive(false);
                        break;
                    }
                }
            }
        }

        // 玩家子弹与敌人子弹相碰
        List<Projectile> enemyProjectiles = new ArrayList<>(projectiles);
        enemyProjectiles.removeAll(playerProjectiles);
        for (Projectile playProjectile : playerProjectiles) {
            if (!playProjectile.isAlive()) {
                continue;
            }
            for (Projectile enemyProjectile : enemyProjectiles) {
                if (enemyProjectile.isAlive() && enemyProjectile.isColliding(playProjectile)) {
                    playProjectile.takeDamage(1);
                    enemyProjectile.takeDamage(1);
                }
            }
        }


        for (Enemy enemy : enemyList) {
            if (!enemy.isAlive()) {
                break;
            }
            if (enemy.isColliding(player)) {
                // 敌人碰到玩家，游戏结束
                player.setHealth(0);
                return;
            }

            for (Bunker bunker : bunkerList) {
                // 敌人碰到掩体，掩体消失
                if (enemy.isColliding(bunker)) {
                    bunker.takeDamage(1);
                }
            }
        }


    }

    private void checkGame() {
        if (!player.isAlive()) {
            System.out.println("Failed: Player has no health!!");
            end = true;
            return;
        }

        List<Enemy> enemyList = renderables.stream().filter(renderable -> renderable instanceof Enemy)
                .map(renderable -> (Enemy) renderable).collect(Collectors.toList());
        if (enemyList.size() == 0) {
            System.out.println("Win!!!!!");
            end = true;
            return;
        }

        for (Enemy enemy : enemyList) {
            if (enemy.getPosition().getY() + enemy.getHeight() + 2 >= 400) {
                end = true;
                System.out.println("Failed: Enemy arrive the bottom!!!");
                break;
            }
        }


    }

    public List<Renderable> getRenderables() {
        return renderables;
    }

    public void removeRenderable(Renderable renderable) {
        renderables.remove(renderable);
    }


    public void leftReleased() {
        this.left = false;
    }

    public void rightReleased() {
        this.right = false;
    }

    public void leftPressed() {
        this.left = true;
    }

    public void rightPressed() {
        this.right = true;
    }

    /**
     * enemy shoot
     */
    public void enemyShoot() {
        int count = (int) renderables.stream().filter(renderable -> renderable instanceof Projectile)
                .filter(renderable -> !((Projectile) renderable).isPlayer()).count();
        if (count >= 3) {
            return;
        }
        List<Projectile> newProjectiles = new ArrayList<>();

        List<Enemy> enemyList = renderables.stream().filter(renderable -> renderable instanceof Enemy)
                .map(renderable -> (Enemy) renderable).collect(Collectors.toList());
        Collections.shuffle(enemyList);
        for (Enemy enemy : enemyList) {
            if (count >= 3) {
                break;
            }
            // 10%的概率shoot
            if (RANDOM.nextInt(100) >= 10) {
                newProjectiles.add(enemy.shoot());
                count++;
            }
        }
        renderables.addAll(newProjectiles);
    }

    public boolean shootPressed() {
        for (Renderable renderable : renderables) {
            if (renderable instanceof Projectile) {
                Projectile projectile = (Projectile) renderable;
                if (projectile.isPlayer()) {
                    return false;
                }
            }
        }
        Projectile projectile = player.shoot();
        renderables.add(projectile);
        return true;
    }

    private void movePlayer() {
        if (left) {
            player.left();
        }

        if (right) {
            player.right();
        }
    }


    private void initEnemies(JSONArray enemies) {
        for (Object o : enemies) {
            JSONObject enemyJson = (JSONObject) o;
            int positionX = getInt(getJSON(enemyJson, "position"), "x");
            int positionY = getInt(getJSON(enemyJson, "position"), "y");
            String projectile = getString(enemyJson, "projectile");
            Projectile.Type type = projectile.equals("fast_straight") ? Projectile.Type.FAST : Projectile.Type.SLOW;

            // created using Builder pattern
            Enemy enemy = Enemy.EnemyBuilder.builder().vector2D(new Vector2D(positionX, positionY)).projetileType(type).build();
            renderables.add(enemy);
        }
    }

    private void initBunkers(JSONArray bunkers) {
        for (Object o : bunkers) {
            JSONObject bunkerJson = (JSONObject) o;
            int positionX = getInt(getJSON(bunkerJson, "position"), "x");
            int positionY = getInt(getJSON(bunkerJson, "position"), "y");
            int sizeX = getInt(getJSON(bunkerJson, "size"), "x");
            int sizeY = getInt(getJSON(bunkerJson, "size"), "y");

            // created using Builder pattern
            Bunker bunker = Bunker.BunkerBuilder.builder().position(new Vector2D(positionX, positionY)).width(sizeX).height(sizeY).build();
            renderables.add(bunker);
        }
    }

    private void initPlayer(JSONObject playerJson) {
        //ignore
        int lives = getInt(playerJson, "lives");
        player.setHealth(lives);
    }

    private void initGameSize(JSONObject game) {
        if (game == null) {
            return;
        }
        sizeX = Integer.parseInt(getString(getJSON(game, "size"), "x"));
        sizeY = Integer.parseInt(getString(getJSON(game, "size"), "y"));

    }

    private JSONObject getJSON(JSONObject json, String key) {
        return (JSONObject) json.get(key);
    }

    private JSONArray getArray(JSONObject json, String key) {
        return (JSONArray) json.get(key);
    }

    private String getString(JSONObject json, String key) {
        return json.get(key).toString();
    }

    private int getInt(JSONObject json, String key) {
        return Integer.parseInt(json.get(key).toString());
    }
}
