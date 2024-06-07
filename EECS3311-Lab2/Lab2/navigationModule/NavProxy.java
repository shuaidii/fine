package navigationModule;

import GPSReceiverModule.GPSReceiver;
import GPSReceiverModule.HoneywellReceiver;
import GPSReceiverModule.RockwellReceiver;
import autopilotModule.Coordinates;
import controlSurfacesModule.AileronActuator;
import controlSurfacesModule.ElevatorActuator;
import controlSurfacesModule.IControlActuator;
import controlSurfacesModule.RudderActuator;
import coordinateComparisonModule.FirstIsBest;
import coordinateComparisonModule.TwoThreeVoting;

import java.util.ArrayList;
import java.util.List;

/**
 * Proxy Pattern
 */
public class NavProxy {

    private NavigationFacade navigationFacade;

    public NavProxy() {
        this.init();
    }

    private void init() {
        List<GPSReceiver> receivers = new ArrayList<>();
        receivers.add(new HoneywellReceiver());
        receivers.add(new HoneywellReceiver());
        receivers.add(new RockwellReceiver());

        List<IControlActuator> actuators = new ArrayList<>();
        actuators.add(new AileronActuator());
        actuators.add(new ElevatorActuator());
        actuators.add(new RudderActuator());
        this.navigationFacade = new NavigationFacade(receivers, new FirstIsBest(), new TwoThreeVoting(), actuators);
    }

    public Coordinates navigate(int currentLat, int currentLn) {
       return navigationFacade.navigate(currentLat, currentLn);
    }
}
