package glue502.software.models;

import java.util.List;

public class ReturnStrategy {
    private String userName;
    private String avatar;
    private String strategyId;
    private String title;
    private String describe;
    private String time;
    private String latitude;
    private String longitude;
    private String userId;

    private List<PostPicture> images;

    public ReturnStrategy(String userName, String avatar,
                          String userId,
                          String strategyId, String title,
                          String describe,
                          String time, String latitude,
                          String longitude, List<PostPicture> images) {
        this.userName = userName;
        this.avatar = avatar;
        this.strategyId = strategyId;
        this.title = title;
        this.describe = describe;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images = images;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ReturnStrategy{" +
                "UserName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", StrategyId='" + strategyId + '\'' +
                ", title='" + title + '\'' +
                ", describe='" + describe + '\'' +
                ", time='" + time + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", images=" + images +
                '}';
    }

    public ReturnStrategy() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public PostPicture getImageFromImages(int i) {
        return images.get(i);
    }
    public List<PostPicture> getImages() {
        return images;
    }

    public void setImages(List<PostPicture> images) {
        this.images = images;
    }
}
