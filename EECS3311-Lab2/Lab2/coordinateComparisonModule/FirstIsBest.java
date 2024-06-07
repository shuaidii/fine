package coordinateComparisonModule;

import autopilotModule.*;

/**
 * Strategy Pattern
 */
public class FirstIsBest implements ICompareCoordsStrategy {

    @Override
    public Coordinates compareCoords(Coordinates gps1, Coordinates gps2, Coordinates gps3) {
        System.out.println(" ********* NO AGREEMENT **********");
        System.out.println(" ********* GPS1 DATA WILL BE USED **********");
        return gps1;
    }
}
