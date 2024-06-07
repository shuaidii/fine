package meta;

public class EmptySquare extends Square{

    public EmptySquare(int index) {
        super(index);
    }

    @Override
    public Player getOwner() {
        return null;
    }

    @Override
    public String toString() {
        return index+ "-EMPTY";
    }
}
