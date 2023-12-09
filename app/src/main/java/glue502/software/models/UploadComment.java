package glue502.software.models;

//用于显示评论区
public class UploadComment {
    private String postId;
    private String userId;
    private String text;
    private String commentId;
    private String time;

    public UploadComment(String postId, String userId, String text, String commentId, String time) {
        this.postId = postId;
        this.userId = userId;
        this.text = text;
        this.commentId = commentId;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
