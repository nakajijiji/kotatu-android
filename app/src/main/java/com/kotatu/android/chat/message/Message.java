package com.kotatu.android.chat.message;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by mayuhei on 2017/05/15.
 */

public class Message {
    protected Type type;
    private String roomId;
    private String from;
    private String to;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public static enum Type {
        CALLME, OFFER, ANSWER, ICE_CANDIDATE;

        @JsonCreator
        public Type from(String str){
            return valueOf(str);
        }
    }
}
