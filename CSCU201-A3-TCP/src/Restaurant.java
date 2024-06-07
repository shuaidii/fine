import java.io.Serializable;

public class Restaurant implements Serializable {
    private int time;
    private String name;
    private String food;

    private Location Location;

    public Restaurant(int time, String name, String food) {
        this.time = time;
        this.name = name;
        this.food = food;
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getFood() {
        return food;
    }

    public Location getLocation() {
        return Location;
    }

    public void setLocation(Location location) {
        Location = location;
    }
}
