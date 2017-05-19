package com.kotatu.android.top.api;

import com.kotatu.android.entity.Lounge;
import com.kotatu.android.entity.Room;
import com.kotatu.android.entity.User;
import com.kotatu.android.entity.UserRoom;

import java.util.List;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class GetRoomsApiResult {
    private Room myRoom;
    private List<Room> lounges;
    private List<Room> userRooms;

    public Room getMyRoom() {
        return myRoom;
    }

    public void setMyRoom(Room myRoom) {
        this.myRoom = myRoom;
    }

    public List<Room> getLounges() {
        return lounges;
    }

    public void setLounges(List<Room> lounges) {
        this.lounges = lounges;
    }

    public List<Room> getUserRooms() {
        return userRooms;
    }

    public void setUserRooms(List<Room> userRooms) {
        this.userRooms = userRooms;
    }
}

