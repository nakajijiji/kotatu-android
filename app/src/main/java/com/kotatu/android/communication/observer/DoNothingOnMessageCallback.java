package com.kotatu.android.communication.observer;

import org.webrtc.DataChannel;

/**
 * Created by mayuhei on 2017/05/25.
 */

public class DoNothingOnMessageCallback implements DefaultDataChannelObserver.OnMessageCallback{
    @Override
    public void call(String socketId, DataChannel.Buffer buffer) {

    }

    @Override
    public void destroy() {

    }
}
