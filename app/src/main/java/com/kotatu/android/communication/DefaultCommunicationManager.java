package com.kotatu.android.communication;

import android.content.Context;
import android.util.Log;

import com.kotatu.android.communication.observer.DefaultDataChannelObserver;
import com.kotatu.android.communication.observer.PlayAudio;

import org.webrtc.DataChannel;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

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
    private PeerConnectionFactory factory;

    @Override
    public void init(Context context){
        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true, null);
        PeerConnectionFactory factory = new PeerConnectionFactory();
        this.factory = factory;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void setMicrophone(boolean on) {
        if(recordTask == null && on) {
            AudioRecordTask recordTask = new AudioRecordTask(new AudioRecordTask.Callback() {
                @Override
                public void call(ShortBuffer buffer) {
                    ByteBuffer bb = ByteBuffer.allocateDirect(buffer.array().length * 2);
                    for (short s : buffer.array()) {
                        bb.putShort(s);
                    }
                    manager.broadcast(bb);
                }
            });
            executor.submit(recordTask);
            this.recordTask = recordTask;
        }else if(!on){
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
        WebRTCConnectionManager manager = new WebRTCConnectionManager(factory, roomId, buildOnMessageCallback());
        manager.connect(signalingServerAddress, getIceServers(iceServerAddresses));
        this.manager = manager;
        setMicrophone(true);
    }

    @Override
    public void disconnect(String roomId) {
        setMicrophone(false);
        this.manager = null;
        this.roomId = null;
        if(executor != null){
            executor.shutdown();
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
