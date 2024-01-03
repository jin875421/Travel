package glue502.software.models;

import java.util.Date;
import java.util.List;

public class TravelReview {
    private String userId;
    private String travelId;
    private List<String> images;
    private String travelName;
    private String date;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getTravelId() {
        return travelId;
    }
    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }
    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }
    public String getTravelName() {
        return travelName;
    }
    public void setTravelName(String travelName) {
        this.travelName = travelName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
