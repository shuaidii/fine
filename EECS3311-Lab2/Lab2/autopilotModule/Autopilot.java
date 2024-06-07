package autopilotModule;

import navigationModule.NavProxy;

public class Autopilot {
    private NavProxy proxy;
    private int nextLat;
    private int nextLon;

    public Autopilot(NavProxy proxy, int nextWaypointLatitude, int nextWaypointLongitude) {
        this.proxy = proxy;
        this.nextLat = nextWaypointLatitude;
        this.nextLon = nextWaypointLongitude;
    }

    /**
     * The point of this method is to send a message to the proxy.
     * @return the same coordinates that the facade is returning to the autopilot (via the proxy)
     */
    public Coordinates navigate() {
        // TODO: complete the implementation of this method
        Coordinates coordinates = proxy.navigate(nextLat, nextLon);
        return coordinates; // change this, as it should return the coordinates that the facade gave to the autopilot.
    }

}
