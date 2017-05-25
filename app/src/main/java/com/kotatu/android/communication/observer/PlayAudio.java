package com.kotatu.android.communication.observer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import org.webrtc.DataChannel;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mayuhei on 2017/05/25.
 */

public class PlayAudio implements DefaultDataChannelObserver.OnMessageCallback{
    private static String TAG = PlayAudio.class.getCanonicalName();
    private Map<String, AudioTrack> trackMap = new HashMap<>();

    @Override
    public void call(String socketId, DataChannel.Buffer buffer) {
        AudioTrack track = trackMap.get(socketId);
        if(track == null){
            track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,4096, AudioTrack.MODE_STREAM );
            track.play();
            trackMap.put(socketId, track);
        }
        ByteBuffer result = buffer.data;
        ShortBuffer sb = result.asShortBuffer();
        int size = sb.limit();
        short[] shortArray = new short[size];
        sb.get(shortArray);
        track.write(shortArray, 0, size);
    }

    @Override
    public void destroy() {
        for(Map.Entry<String, AudioTrack> e : trackMap.entrySet()){
            e.getValue().release();
        }
    }
}
