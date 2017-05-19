package com.kotatu.android.entity;

import java.util.List;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class Lounge extends Room {
    private String name;
    private List<User> members;

    public static Lounge from(String roomId, String name, List<User> members){
        Lounge result = new Lounge();
        result.setRoomId(roomId);
        result.setName(name);
        result.setMembers(members);
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
