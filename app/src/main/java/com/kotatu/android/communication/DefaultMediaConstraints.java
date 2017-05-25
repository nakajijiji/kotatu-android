package com.kotatu.android.communication;

import org.webrtc.MediaConstraints;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class DefaultMediaConstraints extends MediaConstraints{
    public DefaultMediaConstraints(){
        this.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", "true"));
        this.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", "true"));
    }
}
