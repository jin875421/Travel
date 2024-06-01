package glue502.software.models;

public class Follow {
    private String id;
    private String userId;
    private String followId;

    public Follow() {

    }

    public Follow(String id, String userId, String followId) {
        this.id = id;
        this.userId = userId;
        this.followId = followId;
    }

}
