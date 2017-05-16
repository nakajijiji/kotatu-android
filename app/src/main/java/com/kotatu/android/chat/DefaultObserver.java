package com.kotatu.android.chat;

import android.media.AudioManager;
import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.util.JsonSerializer;

import org.json.JSONObject;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoTrack;

import java.util.List;

/**
 * Created by mayuhei on 2017/05/15.
 */

public class DefaultObserver implements PeerConnection.Observer {
    private final String TAG = "sample";

    private Socket socket;
    private VideoRenderer renderer;

    public DefaultObserver(Socket socket, VideoRenderer renderer) {
        this.socket = socket;
        this.renderer = renderer;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG, "pcob1 onSignalingChange() " + signalingState.name());
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
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
        socket.emit(SocketMessageKey.SEND_ICE_CANDIDATE.toString(), JsonSerializer.serialize(message));
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        List<VideoTrack> videoTracks = mediaStream.videoTracks;
        videoTracks.get(0).addRenderer(renderer);
        List<AudioTrack> audioTracks = mediaStream.audioTracks;
        AudioTrack track = audioTracks.get(0);
        track.setEnabled(true);
        Log.d(TAG, "on add stream");
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
