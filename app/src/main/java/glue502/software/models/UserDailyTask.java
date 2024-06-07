package glue502.software.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.List;

public class UserDailyTask {
    public static final int DAILY_TASK_TYPE_DAILY = 0;
    public static final int DAILY_TASK_TYPE_WEEKLY = 1;
    public static final int DAILY_TASK_TYPE_MONTHLY = 2;
    public static final int DAILY_TASK_TYPE_ONCE = 3;

    private String id;
    private String userId;
    private String taskName;
    private int progress;
    private int maxProgress;
    private int reward;
    private boolean completed;
    private Timestamp lastUpdated;
    private int type;
    private String taskIcon;

    public static List<UserDailyTask> parseJson(String responseData) {
        Gson gson = new Gson();
        return gson.fromJson(responseData, new TypeToken<List<UserDailyTask>>(){}.getType());
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTaskIcon() {
        return taskIcon;
    }

    public void setTaskIcon(String taskIcon) {
        this.taskIcon = taskIcon;
    }

    public UserDailyTask() {
    }
    public UserDailyTask(String id, String userId, String taskName, int progress, int maxProgress, int reward, boolean completed, Timestamp lastUpdated, int type, String taskIcon) {
        this.id = id;
        this.userId = userId;
        this.taskName = taskName;
        this.progress = progress;
        this.maxProgress = maxProgress;
        this.reward = reward;
        this.completed = completed;
        this.lastUpdated = lastUpdated;
        this.type = type;
        this.taskIcon = taskIcon;
    }
}
