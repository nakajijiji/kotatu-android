package com.kotatu.android.communication;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.kotatu.android.communication.observer.DefaultDataChannelObserver;
import com.kotatu.android.communication.observer.PlayAudio;

import org.webrtc.DataChannel;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mayuhei on 2017/05/25.
 */

public class DefaultCommunicationManager implements CommunicationManager {
    private static String TAG = DefaultCommunicationManager.class.getCanonicalName();

    private ExecutorService executor;
    private WebRTCConnectionManager manager;
    private String roomId;
    private AudioRecordTask recordTask;
    private DefaultDataChannelObserver.OnMessageCallback callback;
    private PeerConnectionFactory factory;
    private AudioManager audioManager;

    @Override
    public void init(Context context){
        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true);
        PeerConnectionFactory factory = new PeerConnectionFactory();
        this.audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        this.factory = factory;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void setMicrophone(boolean on) {
        if(recordTask == null && on) {
            audioManager.setSpeakerphoneOn(true);
            AudioRecordTask recordTask = new AudioRecordTask(new AudioRecordTask.Callback() {
                @Override
                public void call(ByteBuffer buffer) {
                    manager.broadcast(buffer);
                }
            });
            executor.submit(recordTask);
            this.recordTask = recordTask;
        }else if(!on){
            audioManager.setSpeakerphoneOn(false);
            if(recordTask != null){
                recordTask.setAudioRecordEnabled(false);
                this.recordTask = null;
            }
        }
    }

    @Override
    public void connect(String roomId, String signalingServerAddress, String[] iceServerAddresses) {
        Log.d(TAG, "connecting to:" + roomId);
        this.roomId = roomId;
        DefaultDataChannelObserver.OnMessageCallback callback = buildOnMessageCallback();
        this.callback = callback;
        WebRTCConnectionManager manager = new WebRTCConnectionManager(factory, roomId, callback);
        manager.connect(signalingServerAddress, getIceServers(iceServerAddresses));
        this.manager = manager;
        setMicrophone(true);
    }

    @Override
    public void disconnect(String roomId) {
        org.webrtc.AudioTrack track;
        setMicrophone(false);
        manager.disconnect();
        this.manager = null;
        this.roomId = null;
        if(executor != null){
            executor.shutdown();
        }
        if(callback != null){
            callback.destroy();
        }
    }

    @Override
    public byte[] getStreamData(String roomId, String connectionId) {
        throw new UnsupportedOperationException();
    }

    protected DefaultDataChannelObserver.OnMessageCallback buildOnMessageCallback(){
        return new PlayAudio();
    }

    private List<PeerConnection.IceServer> getIceServers(String[] iceServerAddresses) {
        List<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();
        for(String iceServerAddress : iceServerAddresses) {
            iceServers.add(new PeerConnection.IceServer(iceServerAddress));
        }
        return iceServers;
    }
}
