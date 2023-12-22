package glue502.software.models;

public class MarkerInfo {
    private String markerId;
    private String city;
    private double latitude;
    private double longitude;

    public MarkerInfo(String markerId, String city, double latitude, double longitude) {
        this.markerId = markerId;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public MarkerInfo() {
    }

    @Override
    public String toString() {
        return "MarkerInfo{" +
                "markerId='" + markerId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
