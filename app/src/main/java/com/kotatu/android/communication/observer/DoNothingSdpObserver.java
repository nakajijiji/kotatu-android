package com.kotatu.android.communication.observer;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class DoNothingSdpObserver implements SdpObserver{
    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {

    }

    @Override
    public void onSetSuccess() {

    }

    @Override
    public void onCreateFailure(String s) {

    }

    @Override
    public void onSetFailure(String s) {

    }
}
