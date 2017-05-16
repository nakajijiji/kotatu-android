package com.kotatu.android.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.kotatu.android.util.JsonSerializer;

import org.webrtc.SessionDescription;

import java.util.List;
import java.util.Map;

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
