package meta;

public enum BuildingType {
    A(1),
    B(2),
    C(3),
    D(4),
    E(5),
    F(6),
    G(7),
    H(8);

    private int index;

    BuildingType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
