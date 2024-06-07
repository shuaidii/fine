package invaders.adpter;

import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectile;
import invaders.gameobject.Enemy;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import javafx.scene.input.KeyCode;

import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-10-13
 * Time: 20:30
 */
public class CheatAdapter implements Cheat {
    private Random random = new Random();
    private GameEngine adaptee;

    public CheatAdapter(GameEngine adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void execute(KeyCode code) {
        List<Renderable> readableList = adaptee.getRenderables();
        // 随机移除 所有快的或慢的
        boolean removeFast = random.nextBoolean();
        if (code.equals(KeyCode.P)) {
            // 移除敌人的子弹
            for (Renderable renderable : readableList) {
                if (!renderable.getRenderableObjectName().equals("EnemyProjectile")) {
                    continue;
                }
                EnemyProjectile enemyProjectile = (EnemyProjectile) renderable;
                if (removeFast && enemyProjectile.getProjectileStrategy() instanceof FastProjectileStrategy) {
                    enemyProjectile.takeDamage(enemyProjectile.getHealth());
                    adaptee.getPendingToRemoveRenderable().add(enemyProjectile);
                    adaptee.notifyObservers(enemyProjectile.getScore());
                } else if (!removeFast && enemyProjectile.getProjectileStrategy() instanceof SlowProjectileStrategy) {
                    enemyProjectile.takeDamage(enemyProjectile.getHealth());
                    adaptee.getPendingToRemoveRenderable().add(enemyProjectile);
                    adaptee.notifyObservers(enemyProjectile.getScore());
                }
            }
        }

        if (code.equals(KeyCode.E)) {
            // 移除敌人
            for (Renderable renderable : readableList) {
                if (!renderable.getRenderableObjectName().equals("Enemy")) {
                    continue;
                }
                Enemy enemy = (Enemy) renderable;
                if (removeFast && enemy.getProjectileStrategy() instanceof FastProjectileStrategy) {
                    enemy.takeDamage(enemy.getHealth());
                    adaptee.getPendingToRemoveRenderable().add(enemy);
                    adaptee.notifyObservers(enemy.getScore());
                } else if (!removeFast && enemy.getProjectileStrategy() instanceof SlowProjectileStrategy) {
                    enemy.takeDamage(enemy.getHealth());
                    adaptee.getPendingToRemoveRenderable().add(enemy);
                    adaptee.notifyObservers(enemy.getScore());
                }
            }
        }
    }
}
