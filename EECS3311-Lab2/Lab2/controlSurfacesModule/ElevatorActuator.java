package controlSurfacesModule;

public class ElevatorActuator implements IControlActuator{
    @Override
    public void update(int curLat, int curLon, int nextLat, int nextLon) {
        System.out.printf("Elevator Actuator Notified with values  %d %d %d %d%n", nextLat, nextLon, curLat, curLon);
    }
}
