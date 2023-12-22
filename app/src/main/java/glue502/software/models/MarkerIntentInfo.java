package glue502.software.models;

import java.io.Serializable;

public class MarkerIntentInfo implements Serializable {
    private String name;
    private Double latitude;
    private Double longitude;

    private String selectedKey;

    private String selectedCity;

    private String selectedDistrict;

    public MarkerIntentInfo(String name, Double latitude, Double longitude, String selectedKey, String selectedCity, String selectedDistrict) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.selectedKey = selectedKey;
        this.selectedCity = selectedCity;
        this.selectedDistrict = selectedDistrict;
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

    public String getSelectedKey() {
        return selectedKey;
    }

    public void setSelectedKey(String selectedKey) {
        this.selectedKey = selectedKey;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getSelectedDistrict() {
        return selectedDistrict;
    }

    public void setSelectedDistrict(String selectedDistrict) {
        this.selectedDistrict = selectedDistrict;
    }

    public String toString() {
        return "MarkerIntentInfo{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
