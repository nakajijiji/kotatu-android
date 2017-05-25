package com.kotatu.android.communication.observer;

import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.communication.SocketMessageKey;
import com.kotatu.android.communication.message.IceCandidateMessage;
import com.kotatu.android.util.JsonSerializer;

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

import java.util.List;

/**
 * Created by mayuhei on 2017/05/15.
 */

public class DefaultObserver implements PeerConnection.Observer {
    private static final String TAG = DefaultObserver.class.getCanonicalName();

    private Socket socket;
    private String roomId;
    private String socketId;
    private DefaultDataChannelObserver.OnMessageCallback onMessageCallback;

    public DefaultObserver(Socket socket, String roomId, String socketId, DefaultDataChannelObserver.OnMessageCallback callback) {
        this.roomId = roomId;
        this.socket = socket;
        this.onMessageCallback = callback;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG, "pcob1 onSignalingChange() " + signalingState.name());
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
//        if(iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED){
//            mediaStream.videoTracks.get(0).removeRenderer(renderer);
//        }
        Log.d(TAG, "pcob1 onIceConnectionChange() " + iceConnectionState.name());
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(TAG, "pcob1 onIceGatheringChange() " + iceGatheringState.name());
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.d(TAG, "pcob1 onIceCandidate()");
        IceCandidateMessage message = new IceCandidateMessage();
        message.setRoomId(roomId);
        message.setIceCandidate(iceCandidate);
        socket.emit(SocketMessageKey.MESSAGE.toString(), JsonSerializer.serialize(message));
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        List<AudioTrack> audioTracks = mediaStream.audioTracks;
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.d(TAG, "pcob1 onRemoveStream");
    }

    /*
    when remote peer call createDataChannel, this method is called
     */
    @Override
    public void onDataChannel(DataChannel dataChannel) {
        dataChannel.registerObserver(new DefaultDataChannelObserver(socketId, dataChannel, onMessageCallback));
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG, "pcob1 onRenegotiationNeeded()");
    }
}
