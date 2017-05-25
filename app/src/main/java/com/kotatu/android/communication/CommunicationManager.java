package com.kotatu.android.communication;

import android.content.Context;

/**
 * Created by mayuhei on 2017/05/25.
 */

public interface CommunicationManager {
    void init(Context context);

    void setMicrophone(boolean on);

    void connect(String roomId, String signalingServerAddress, String[] iceServerAddresses);

    void disconnect(String roomId);

    byte[] getStreamData(String roomId, String connectionId);
}
