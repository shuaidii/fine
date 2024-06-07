package invaders.logic;

import invaders.physics.Collider;

public interface Damagable {

    public void takeDamage(double amount);

    public double getHealth();

    public boolean isAlive();
}
