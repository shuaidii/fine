package invaders.physics;

/**
 * A utility class for storing position information
 */
public class Vector2D implements Cloneable {

	private double x;
	private double y;

	public Vector2D(double x, double y){
		this.x = x;
		this.y = y;
	}

    @Override
    public Vector2D clone() throws CloneNotSupportedException {
        return (Vector2D)super.clone();
    }

    public double getX(){
		return this.x;
	}

	public double getY(){
		return this.y;
	}

	public void setX(double x){
		this.x = x;
	}

	public void setY(double y){
		this.y = y;
	}
}
