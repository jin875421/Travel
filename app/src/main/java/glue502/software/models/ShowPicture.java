package glue502.software.models;

import java.util.Date;
import java.util.List;

public class ShowPicture {

    private List<String> picturePath;
    private String travelDate;
    private String placeName;
    private String placeId;

    public ShowPicture() {
    }

    public ShowPicture(List<String> picturePath, String travelDate, String placeName) {
        this.picturePath = picturePath;
        this.travelDate = travelDate;
        this.placeName = placeName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<String> getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(List<String> picturePath) {
        this.picturePath = picturePath;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
