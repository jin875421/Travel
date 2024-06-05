package glue502.software.models;

public class Follow {
    private String id;
    private String userId;
    private String followId;
    private String groupOf = "全部";

    public Follow() {

    }

    public Follow(String id, String userId, String followId) {
        this.id = id;
        this.userId = userId;
        this.followId = followId;
    }
    public Follow(String id, String userId, String followId, String groupOf) {
        this.id = id;
        this.userId = userId;
        this.followId = followId;
        this.groupOf = groupOf;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFollowId() {
        return followId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
    }

    public String getGroupOf() {
        return groupOf;
    }

    public void setGroupOf(String groupOf) {
        this.groupOf = groupOf;
    }
    public String toString() {
        return "Follow{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", followId='" + followId + '\'' +
                ", groupOf='" + groupOf + '\'' +
                '}';
    }
}
