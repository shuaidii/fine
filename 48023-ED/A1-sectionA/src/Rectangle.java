
public class Rectangle {
    private int height;
    private int width;

    public Rectangle() {
        this.height = 1;
        this.width = 1;
    }

    public Rectangle(int length) {
        this.height = length;
        this.width = length;
    }

    public Rectangle(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public int getShortSide() {
        return Math.min(width, height);
    }

    public int getLongSide () {
        return Math.max(width, height);
    }

    public boolean isSquare() {
        return width == height;
    }

    public int area() {
        return width * height;
    }

    public double diagonal() {
        return Math.sqrt(width * width + height * height);
    }

    public boolean bigger(Rectangle rectangle) {
        return area() > rectangle.area();
    }
}
