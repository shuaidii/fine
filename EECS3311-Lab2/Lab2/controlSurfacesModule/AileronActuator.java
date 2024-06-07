package controlSurfacesModule;

public class AileronActuator implements IControlActuator{
    @Override
    public void update(int curLat, int curLon, int nextLat, int nextLon) {
        System.out.printf("Aileron Actuator Notified with values  %d %d %d %d%n", nextLat, nextLon, curLat, curLon);
    }
}
