package glue502.software.models;

import java.io.Serializable;

public class PostWithUserInfo implements Serializable {
    private Post post;
    private UserInfo userInfo;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    public PostWithUserInfo(Post post,UserInfo userInfo){
        this.post = post;
        this.userInfo =userInfo;
    }
}
