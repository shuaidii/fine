package coordinateComparisonModule;

import autopilotModule.*;

/**
 * Strategy Pattern
 */
public interface ICompareCoordsStrategy {
    public Coordinates compareCoords(Coordinates gps1, Coordinates gps2, Coordinates gps3);
}
