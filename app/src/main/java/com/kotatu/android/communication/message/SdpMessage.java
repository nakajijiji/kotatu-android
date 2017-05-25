package com.kotatu.android.communication.message;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import org.webrtc.SessionDescription;

/**
 * Created by mayuhei on 2017/05/15.
 */

public class SdpMessage extends Message {
    private DeserializableSessionDescription sessionDescription;

    @JsonGetter("sessionDescription")
    public DeserializableSessionDescription getSerializableSessionDescription(){
        return sessionDescription;
    }

    @JsonSetter("sessionDescription")
    public void setSerializableSessionDescription(DeserializableSessionDescription sessionDescription) {
        this.sessionDescription = sessionDescription;
    }

    @JsonIgnore
    public SessionDescription getSessionDescription(){
        if(sessionDescription == null){
            return null;
        }
        return sessionDescription.toSessionDescription();
    }

    @JsonIgnore
    public void setSessionDescription(SessionDescription sessionDescription){
        DeserializableSessionDescription result = new DeserializableSessionDescription();
        result.description = sessionDescription.description;
        result.type = sessionDescription.type.toString();
        this.sessionDescription = result;
    }

    public static class DeserializableSessionDescription  {
        public String type;
        public String description;

        public SessionDescription toSessionDescription(){
            return new SessionDescription(SessionDescription.Type.valueOf(type), description);
        }
    }
}
