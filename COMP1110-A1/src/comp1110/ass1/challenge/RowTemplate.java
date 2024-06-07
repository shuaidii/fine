package comp1110.ass1.challenge;

public enum RowTemplate { // defines the rows for the challenges
    A('A',new boolean[] {true,true,false,true,true}),
    B('B',new boolean[] {true,true,true,true,false}),
    C('C',new boolean[] {true,true,true,false,true}),
    D('D',new boolean[] {false,true,true,true,true}),
    E('E',new boolean[] {true,false,true,true,true}),
    ONE('1',new boolean[] {false,true,true,true,true}),
    TWO('2',new boolean[] {true,true,false,false,true}),
    THREE('3',new boolean[] {true,false,true,true,true}),
    FOUR('4',new boolean[] {true,false,true,false,true}),
    FIVE('5',new boolean[] {false,true,true,true,true});

    private final char encoding;

    // The places caterpillar segments can go in that row
    private final boolean[] spotRow;

    RowTemplate(char encoding, boolean[] spotRow) {
        this.encoding = encoding;
        this.spotRow = spotRow;
    }

    public static RowTemplate decodeRow(char encoding) {
        for (RowTemplate rowTemplate : RowTemplate.values()) {
            if (rowTemplate.encoding == encoding) {
                return rowTemplate;
            }
        }
        return null;
    }

    public boolean[] getRow() {
        return this.spotRow;
    }
}
