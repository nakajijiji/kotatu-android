package com.kotatu.android.chat;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import org.webrtc.IceCandidate;

/**
 * Created by mayuhei on 2017/05/15.
 */

public class IceCandidateMessage extends Message {
    private SerializableIceCandidate iceCandidate;

    @JsonGetter("iceCandidate")
    public SerializableIceCandidate getSerializableIceCandidate(){
        return iceCandidate;
    }

    @JsonSetter("iceCandidate")
    public void setSerializableIceCandidate(SerializableIceCandidate iceCandidate){
        this.iceCandidate = iceCandidate;
    }

    @JsonIgnore
    public IceCandidate getIceCandidate() {
        return iceCandidate.toIceCandidate();
    }

    @JsonIgnore
    public void setIceCandidate(IceCandidate iceCandidate) {
        SerializableIceCandidate result = new SerializableIceCandidate();
        result.sdp = iceCandidate.sdp;
        result.sdpMid = iceCandidate.sdpMid;
        result.sdpMLineIndex = iceCandidate.sdpMLineIndex;
        this.iceCandidate = result;
    }

    public static class SerializableIceCandidate {
        public String sdpMid;
        public int sdpMLineIndex;
        public String sdp;

        public IceCandidate toIceCandidate(){
            return new IceCandidate(sdpMid, sdpMLineIndex, sdp);
        }
    }
}
