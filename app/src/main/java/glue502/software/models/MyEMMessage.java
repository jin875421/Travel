package glue502.software.models;

public class MyEMMessage {
    private String message;
    private String username;
    private String time;
    private String avatar;

    public MyEMMessage(String message, String username, String time, String avatar) {
        this.message = message;
        this.username = username;
        this.time = time;
        this.avatar = avatar;
    }

    public MyEMMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
