package autopilotModule;

import java.util.Objects;

public class Coordinates {
	private int latitude;
	private int longitude;
	
	public Coordinates(int latitude, int longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return latitude == that.latitude && longitude == that.longitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    public int getLatitude() {
		return latitude;
	}
	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}
	public int getLongitude() {
		return longitude;
	}
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
}
