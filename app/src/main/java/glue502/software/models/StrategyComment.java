package glue502.software.models;

import java.io.Serializable;
import java.util.List;

public class StrategyComment implements Serializable {
    private String commentId;
    private String username;
    private String postId;
    private String comment;
    private String uploadTime;
    private String avatar;
    private String userId;
    private List<ReturnStrategyCommentRespond> returnStrategyCommentResponds;

    @Override
    public String toString() {
        return "StrategyComment{" +
                "commentId='" + commentId + '\'' +
                ", username='" + username + '\'' +
                ", postId='" + postId + '\'' +
                ", comment='" + comment + '\'' +
                ", uploadTime='" + uploadTime + '\'' +
                ", avatar='" + avatar + '\'' +
                ", userId='" + userId + '\'' +
                ", returnCommentResponds=" + returnStrategyCommentResponds +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ReturnStrategyCommentRespond> getReturnStrategyCommentResponds() {
        return returnStrategyCommentResponds;
    }

    public void setReturnStrategyCommentResponds(List<ReturnStrategyCommentRespond> returnStrategyCommentResponds) {
        this.returnStrategyCommentResponds = returnStrategyCommentResponds;
    }
}
