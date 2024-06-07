package controlSurfacesModule;

public interface IControlActuator {
    public void update(int curLat, int curLon, int nextLat, int nextLon);
    // in the implementing classes, you must mention the hardware component that
    // the actuator corresponds to, as well as the coordinate values inputted
    // note that nextLat and nextLon are the same values that the Autopilot received
    // from the client NavigationServer,
    // and that you'll need to retrieve them from the Autopilot somehow
    // please refer to SampleOutput.txt
}
