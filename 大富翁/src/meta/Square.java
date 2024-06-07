package meta;

public abstract class Square {
    protected int index;

    public Square(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public abstract Player getOwner();

    public abstract String toString();
}
