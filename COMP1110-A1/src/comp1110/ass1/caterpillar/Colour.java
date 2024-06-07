package comp1110.ass1.caterpillar;

/**
 * Colour of caterpillar and spot requirements
 *
 * WILDCARD is only used for spot requirements, cannot be used for caterpillar
 */
public enum Colour {
    GREEN,
    TURQUOISE,
    YELLOW,
    WILDCARD; // Spot can take head of any colour caterpillar
    
    public static Colour fromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'T' -> TURQUOISE;
            case 'G' -> GREEN;
            case 'Y' -> YELLOW;
            case 'X' -> WILDCARD;
            default -> null;
        };
    }

}
