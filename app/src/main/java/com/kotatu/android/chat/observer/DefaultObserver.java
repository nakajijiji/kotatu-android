package com.kotatu.android.chat.observer;

import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.chat.DefaultDataChannelObserver;
import com.kotatu.android.chat.SocketMessageKey;
import com.kotatu.android.chat.message.IceCandidateMessage;
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
    private MediaStream mediaStream;

    public DefaultObserver(Socket socket) {
        this.socket = socket;
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
        message.setRoomId("1");
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

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        dataChannel.registerObserver(new DefaultDataChannelObserver(dataChannel));
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG, "pcob1 onRenegotiationNeeded()");
    }
}
