package meta;

public class StartSquare extends EmptySquare {
    public StartSquare(int index) {
        super(index);
    }

    @Override
    public String toString() {
        return index + "-GO";
    }
}
