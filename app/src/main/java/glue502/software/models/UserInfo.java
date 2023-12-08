package glue502.software.models;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String password;
    private String sex;
    private String userId;
    private String avatar;
    private String email;
    private String code;
    private String userName;
    private String status;
    private String userPhoneNumber;
    private float a;



    public UserInfo() {}

    public UserInfo(String email) {
        this.email = email;
    }
    public UserInfo(String userPhoneNumber,int a) {
        this.userPhoneNumber = userPhoneNumber;
    }
    public UserInfo(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
    public UserInfo(String userId, String userName,boolean abc) {
        this.userId = userId;
        this.userName = userName;
    }
    public UserInfo(String email, String code,int a) {
        this.email = email;
        this.code = code;
    }
    public UserInfo(String userPhoneNumber, String code,double ad) {
        this.userPhoneNumber = userPhoneNumber;
        this.code = code;
    }
    public UserInfo(String password, String email, String code) {
        this.password = password;
        this.email = email;
        this.code = code;
    }
    public UserInfo(String password, String userPhoneNumber, String code,int a) {
        this.password = password;
        this.userPhoneNumber = userPhoneNumber;
        this.code = code;
    }
    public UserInfo(String userId, String password, String email,String userNickname,String userPhoneNumber) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.userName=userNickname;
        this.userPhoneNumber=userPhoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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
    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
