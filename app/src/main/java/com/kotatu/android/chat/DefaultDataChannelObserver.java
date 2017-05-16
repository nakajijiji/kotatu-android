package com.kotatu.android.chat;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.PeerConnection;

import java.nio.ByteBuffer;

/**
 * Created by mayuhei on 2017/05/15.
 */

public class DefaultDataChannelObserver implements DataChannel.Observer{
    private final String TAG = "sample";
    private DataChannel dataChannel;

    public DefaultDataChannelObserver(DataChannel dataChannel){
        this.dataChannel = dataChannel;
    }

    @Override
    public void onStateChange() {
        Log.d(TAG, "da1o onStateChange() " + dataChannel.state().name());

        if (dataChannel.state() == DataChannel.State.OPEN) {
            String data = "from datachannel";
            ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
            dataChannel.send(new DataChannel.Buffer(buffer, false));
        }
    }

    @Override
    public void onMessage(DataChannel.Buffer buffer) {
        Log.d(TAG, "da1o onMessage()");

        if (buffer.binary == false) {
            int limit = buffer.data.limit();
            byte[] datas = new byte[limit];
            buffer.data.get(datas);
            String tmp = new String(datas);
            Log.d(TAG, tmp);
        }
    }
}
