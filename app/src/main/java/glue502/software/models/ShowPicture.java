package glue502.software.models;

import java.util.Date;
import java.util.List;

public class ShowPicture {

    private List<String> picturePath;
    private Date travelDate;
    private String placeName;

    public ShowPicture() {
    }

    public ShowPicture(List<String> picturePath, Date travelDate, String placeName) {
        this.picturePath = picturePath;
        this.travelDate = travelDate;
        this.placeName = placeName;
    }

    public List<String> getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(List<String> picturePath) {
        this.picturePath = picturePath;
    }

    public Date getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(Date travelDate) {
        this.travelDate = travelDate;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
