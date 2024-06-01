package glue502.software.models;

import com.baidu.mapapi.model.LatLng;

public class Speech {
    private String text;
    private String userId;
    private double latitude;
    private double longitude;

    public Speech(String text, String userId, double latitude, double longitude) {
        this.text = text;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Speech() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "Speech{" +
                "text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
