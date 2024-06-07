package navigationModule;

import autopilotModule.*;
import controlSurfacesModule.*;
import coordinateComparisonModule.*;
import GPSReceiverModule.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade Pattern
 */
public class NavigationFacade {
    private List<GPSReceiver> gpsReaders;
    private ICompareCoordsStrategy firstIsBest;
    private ICompareCoordsStrategy twoThreeVoting;
    private GPSDataSubject gpsDataSubject;

    /*
     * The Navigation Façade does the following sequence of steps (see Façade pattern)
     * a. Send a request to GPSReceiver object #1 to get the first reading
     * b. Send a request to GPSReceiver object #2 to get the second reading
     * c. Send a request to GPSReceiver object #3 to get the third reading
     * !! Don’t forget, two of the GPSReceivers are Honeywell brand,
     * and the other one is Rockwell brand.
     * d. Apply the comparison strategy/strategies to get the resulting coordinates
     * (see the Strategy pattern)
     * e. Send the coordinates to the GPS Date Subject
     * f. Update the Autopilot with the new coordinates
     */
    public NavigationFacade(List<GPSReceiver> gpsReaders, ICompareCoordsStrategy firstIsBest,
                            ICompareCoordsStrategy twoThreeVoting, List<IControlActuator> actuators) {
        this.gpsReaders = gpsReaders;
        this.firstIsBest = firstIsBest;
        this.twoThreeVoting = twoThreeVoting;

        this.gpsDataSubject = new GPSDataSubject();
        this.gpsDataSubject.register(actuators);
    }

    public Coordinates navigate(int currentLat, int currentLn) {
        List<Coordinates> data = new ArrayList<>();
        data.add(gpsReaders.get(0).read());
        data.add(gpsReaders.get(1).read());
        data.add(gpsReaders.get(2).read());

        Coordinates nextCoords = compareGPSData(data);
        gpsDataSubject.update(currentLat, currentLn, nextCoords.getLatitude(), nextCoords.getLongitude());
        return nextCoords;
    }

    public Coordinates compareGPSData(List<Coordinates> data) {
        ICompareCoordsStrategy compareCoordsStrategy = getCompareCoordsStrategy(data.get(0), data.get(1), data.get(2));
        return compareCoordsStrategy.compareCoords(data.get(0), data.get(1), data.get(2));
    }

    /**
     * This is used as strategy pattern, as gps1、gps2 and gps3 with different cases returning different CompareCoordsStrategy
     */
    private ICompareCoordsStrategy getCompareCoordsStrategy(Coordinates gps1, Coordinates gps2, Coordinates gps3) {
        System.out.println(" ********* INVOKING VOTING COMPARISON **********");
        if (gps1.equals(gps2) || gps1.equals(gps3) || gps2.equals(gps3)) {
            return twoThreeVoting;
        }
        return firstIsBest;
    }

}
