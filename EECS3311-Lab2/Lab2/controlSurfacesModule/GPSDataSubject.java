package controlSurfacesModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer pattern
 */
public class GPSDataSubject {
    private List<IControlActuator> actuatorList = new ArrayList<>();

    /*
     * This is Observer pattern, as GPSDataSubject is observer and the actuatorList are the subjects.
     * When the GPSDataSubject get new data by update method, the subjects of the actuatorList will be updated.
     */
    public void register(List<IControlActuator> actuators) {
        actuatorList.addAll(actuators);
    }

    public void update(int curLat, int curLon, int nextLat, int nextLon) {
        for (IControlActuator actuator : actuatorList) {
            actuator.update(curLat, curLon, nextLat, nextLon);
        }
    }
}
