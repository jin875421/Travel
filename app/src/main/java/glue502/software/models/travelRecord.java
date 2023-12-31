package glue502.software.models;

import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class travelRecord implements Serializable {
    private String userId;//用户id
    private String placeId;//位置id
    private String travelId;//整个旅程的id
    private String placeName;//小的名称，也就是标题
    private String travelName;//大的名称
    private List<String> image;//旅行的图片
    private String content;//包含的内容
    private String createTime;//时间

    private int pictureNumber;//图片的数量

    public int getPictureNumber() {
        return pictureNumber;
    }

    public void setPictureNumber(int pictureNumber) {
        this.pictureNumber = pictureNumber;
    }

    public travelRecord() {

    }
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getTravelName() {
        return travelName;
    }

    public void setTravelName(String travelName) {
        this.travelName = travelName;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
    public String toString(){
        return "travelRecord{" +
                "userId='" + userId + '\'' +
                ", placeId='" + placeId + '\'' +
                ", travelId='" + travelId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", travelName='" + travelName + '\'' +
                ", image=" + image +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", pictureNumber=" + pictureNumber ;
            }
    public void addImage(String imageUrl) {
        if (image == null) {
            image = new ArrayList<>();
        }
        image.add(imageUrl);
        // Update pictureNumber to reflect the new number of images
    }
    public void addImage(int tag, String newImageUrl) {
        if (image != null && tag >= 0 && tag < image.size()) {
            image.set(tag, newImageUrl);
        }
    }

    // Method to remove an image from the image list
    public void removeImage(String imageUrl) {
        if (image != null) {
            image.remove(imageUrl);
            // Update pictureNumber to reflect the new number of images
        }
    }
    public void removeAllImages() {
        if (image != null) {
            image.clear();
        }
    }

}
