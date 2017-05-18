package com.kotatu.android.chat.media;

import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;

/**
 * Created by mayuhei on 2017/05/17.
 */

public abstract class MediaStreamFactory {
    protected PeerConnectionFactory factory;

    public MediaStreamFactory(PeerConnectionFactory factory){
        this.factory = factory;
    }

    public abstract MediaStream create();
}
