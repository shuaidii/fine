package controlSurfacesModule;

public class RudderActuator implements IControlActuator{
    @Override
    public void update(int curLat, int curLon, int nextLat, int nextLon) {
        System.out.printf("Rudder Actuator Notified with values  %d %d %d %d%n", nextLat, nextLon, curLat, curLon);
    }
}
