package glue502.software.models;

import java.io.Serializable;

public class ReturnStrategyCommentRespond implements Serializable {
    private String userName;
    private String text;
    private String time;
    private String avatar;

    public ReturnStrategyCommentRespond(String userName, String text, String time, String avatar) {
        this.userName = userName;
        this.text = text;
        this.time = time;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "ReturnCommentRespond{" +
                "userName='" + userName + '\'' +
                ", text='" + text + '\'' +
                ", time='" + time + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
