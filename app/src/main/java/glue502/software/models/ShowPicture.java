package glue502.software.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//这个类用于接受在显示相册功能中，从后端传递过来的，按照日期分类的照片信息
public class ShowPicture {

    private String placeName;//照片对应的地点
    private List<String> picturePath;//照片的路径
    private Date travelDate;//照片的日期

    public ShowPicture() {
    }

    public ShowPicture(String placeName, List<String> picturePath, Date travelDate) {
        this.placeName = placeName;
        this.picturePath = picturePath;
        this.travelDate = travelDate;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
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

}
