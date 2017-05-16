package com.kotatu.android.chat;

import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by mayuhei on 2017/05/15.
 */
/*
I don't think this is a good name...
 */
public class RoomSocket {
    private String roomId;
    private Socket socket;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
