package GAME.enums;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-04-15
 * Time: 17:02
 */
public enum PieceType {
    SETTLER("S"), VILLAGE("T"),;

    private String shortage;

    PieceType(String shortage) {
        this.shortage = shortage;
    }

    public String getShortage() {
        return shortage;
    }

    public static PieceType parse(String shortage) {
        for (PieceType pieceType : values()) {
            if (pieceType.name().equalsIgnoreCase(shortage) || pieceType.getShortage().equalsIgnoreCase(shortage)) {
                return pieceType;
            }
        }
        return null;
    }
}
