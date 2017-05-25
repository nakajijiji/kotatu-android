package com.kotatu.android.entity;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class UserRoom extends Room {
    private User user;

    public static UserRoom from(String roomId, User user){
        UserRoom result = new UserRoom();
        result.setRoomId(roomId);
        result.setUser(user);
        return result;
    }

    @Override
    public String getName(){
        return user.getScreenName();
    }

    @Override
    public List<User> getMembers(){
        return Arrays.asList(user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
