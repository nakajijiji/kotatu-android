package com.kotatu.android.entity;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class User {
    private Long id;
    private String screenName;
    private String imageUrl;

    public static User from(Long id, String screenName, String imageUrl){
        User user = new User();
        user.id = id;
        user.screenName = screenName;
        user.imageUrl = imageUrl;
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
