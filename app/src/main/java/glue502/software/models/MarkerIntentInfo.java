package glue502.software.models;

import java.io.Serializable;

public class MarkerIntentInfo implements Serializable {
    private String name;
    private Double latitude;
    private Double longitude;

    public MarkerIntentInfo(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getName() {
        return name;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public String toString() {
        return "MarkerIntentInfo{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
