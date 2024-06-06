package glue502.software.models;

public class UserExtraInfo {
    private String userId;
    private String followGroupInfo;
    private String achievement;
    private int experience;
    private int postCount;
    private int liked;
    private int collected;
    private int level;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFollowGroupInfo() {
        return followGroupInfo;
    }

    public void setFollowGroupInfo(String followGroupInfo) {
        this.followGroupInfo = followGroupInfo;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getCollected() {
        return collected;
    }

    public void setCollected(int collected) {
        this.collected = collected;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public UserExtraInfo() {

    }

    public UserExtraInfo(String userId, String followGroupInfo) {
        this.userId = userId;
        this.followGroupInfo = followGroupInfo;
    }

    @Override
    public String toString() {
        return "UserExtraInfo{" +
                "userId='" + userId + '\'' +
                ", followGroupInfo='" + followGroupInfo + '\'' +
                ", achievement='" + achievement + '\'' +
                ", experience=" + experience +
                ", postCount=" + postCount +
                ", liked=" + liked +
                ", collected=" + collected +
                ", level=" + level +
                '}';
    }
}
