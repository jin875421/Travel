package glue502.software.models;

import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.MediaType;

public class ImageEntity implements OpenImageUrl {
    public String photoUrl;
    public String smallPhotoUrl;
    public String coverUrl;
    public String smallCoverUrl;
    public String videoUrl;
    public int resouceType;
    public String path;

    public ImageEntity(String photoUrl, String smallPhotoUrl, String coverUrl, String smallCoverUrl, String videoUrl, int resouceType,String path) {
        this.photoUrl = photoUrl;
        this.smallPhotoUrl = smallPhotoUrl;
        this.coverUrl = coverUrl;
        this.smallCoverUrl = smallCoverUrl;
        this.videoUrl = videoUrl;
        this.resouceType = resouceType;
        this.path=path;
    }

    @Override
    public String getImageUrl() {
        return resouceType == 1 ? coverUrl : photoUrl;
    }

    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public String getCoverImageUrl() {
        return resouceType == 1 ? smallCoverUrl : smallPhotoUrl;
    }

    @Override
    public MediaType getType() {
        return resouceType == 1 ? MediaType.VIDEO : MediaType.IMAGE;
    }
    public String getPath(){
        return path;
    }
}

