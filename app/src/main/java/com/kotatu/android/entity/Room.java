package com.kotatu.android.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class Room {
    private String roomId;
    private String name;
    private List<User> members;
    private String imageUrl;

    public static Room userRoom(String roomId, User user){
        Room result = new Room();
        result.roomId = roomId;
        result.name = user.getScreenName();
        result.members = Arrays.asList(user);
        result.imageUrl = user.getImageUrl();
        return result;
    }

    public static Room lounge(String roomId, String name, String imageUrl, List<User> members){
        Room result = new Room();
        result.roomId = roomId;
        result.name = name;
        result.members = members;
        result.imageUrl = imageUrl;
        return result;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
