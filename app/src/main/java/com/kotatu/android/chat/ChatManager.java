package com.kotatu.android.chat;

import org.webrtc.PeerConnection;

/**
 * Created by mayuhei on 2017/05/17.
 */

public class ChatManager {
    private PeerConnection.Observer observer;
    private ConnectionManager signalingManager;

    public ChatManager(PeerConnection.Observer observer, ConnectionManager signalingManager){
        this.observer = observer;
        this.signalingManager = signalingManager;
    }
}
