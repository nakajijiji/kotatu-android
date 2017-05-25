package com.kotatu.android.chat;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.PeerConnection;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Created by mayuhei on 2017/05/15.
 */

public class DefaultDataChannelObserver implements DataChannel.Observer{
    private final String TAG = "sample";
    private DataChannel dataChannel;
    private AudioTrack track;

    public DefaultDataChannelObserver(DataChannel dataChannel){
        this.dataChannel = dataChannel;
        this.track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,4096, AudioTrack.MODE_STREAM );
        track.play();
    }

    @Override
    public void onStateChange() {
        Log.d(TAG, "da1o onStateChange() " + dataChannel.state().name());

//        if (dataChannel.state() == DataChannel.State.OPEN) {
//            String data = "from datachannel";
//            ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
//            dataChannel.send(new DataChannel.Buffer(buffer, false));
//        }
    }

    @Override
    public void onMessage(DataChannel.Buffer buffer) {
        Log.d(TAG, "da1o onMessage()");
        ByteBuffer result = buffer.data;
        ShortBuffer sb = result.asShortBuffer();
        int size = sb.limit();
        short[] shortArray = new short[size];
        sb.get(shortArray);
        track.write(shortArray, 0, size);
        Log.d(TAG, "da1o done");
    }
}
