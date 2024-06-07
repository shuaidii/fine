package GAME.enums;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2023-04-15
 * Time: 16:58
 */
public enum PhaseType {

    Exploration("E"),
    Settlement("S"),
    ;
    private String phase;

    PhaseType(String phase) {
        this.phase = phase;
    }

    public String getPhase() {
        return phase;
    }

    public static PhaseType parse(String phase) {
        for (PhaseType phaseType : values()) {
            if (phaseType.name().equalsIgnoreCase(phase) || phaseType.getPhase().equalsIgnoreCase(phase)) {
                return phaseType;
            }
        }
        return null;
    }
}
