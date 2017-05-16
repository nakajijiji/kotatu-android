package com.kotatu.android.chat;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.util.JsonSerializer;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mayuhei on 2017/05/17.
 */

public class ConnectionManager {
    private PeerConnectionFactory factory;
    private PeerConnection connection;
    private PeerConnection.Observer observer;
    private Socket socket;
    private SdpObserver sdpObserver;

    public ConnectionManager(PeerConnectionFactory factory, PeerConnection.Observer observer, Socket socket){
        this.factory = factory;
        this.socket = socket;
        this.observer = observer;
    }

    public void reconnect(List<PeerConnection.IceServer> iceServers, String roomId){
        this.connection = factory.createPeerConnection(iceServers, new MediaConstraints(), observer);
        this.sdpObserver = new DefaultSdpObserver(connection, socket);
        socket.connect();
        socket.emit(SocketMessageKey.JOIN.toString(), roomId);
        socket.on(SocketMessageKey.SEND_SDP.toString(), new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(this.getClass().getName(), "RECIEVE SDP");
                if (args.length != 1) {
                    throw new IllegalStateException();
                }
                SdpMessage sdpMessage = JsonSerializer.deserialize(args[0].toString(), SdpMessage.class);
                connection.setRemoteDescription(sdpObserver, sdpMessage.getSessionDescription());
                if(sdpMessage.getSessionDescription().type == SessionDescription.Type.OFFER) {
                    answer();
                }
            }
        });
        socket.on(SocketMessageKey.SEND_ICE_CANDIDATE.toString(), new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(this.getClass().getName(), "RECIEVE ICE_CANDIDATE");
                if (args.length != 1) {
                    throw new IllegalStateException();
                }
                IceCandidateMessage message = JsonSerializer.deserialize(args[0].toString(), IceCandidateMessage.class);
                connection.addIceCandidate(message.getIceCandidate());
            }
        });
    }

    public void disConnect(){
        if (connection != null) {
            connection.close();
        }
        if(socket.connected()) {
            socket.disconnect();
        }
    }

    private SdpObserver build(PeerConnection connection){
        return new DefaultSdpObserver(connection, socket);
    }

    public void update(PeerConnection connection){
        this.connection = connection;
        this.sdpObserver = build(connection);
    }

    public void offer(){
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", "true"));
        connection.createOffer(sdpObserver, sdpMediaConstraints);
    }

    public void answer(){
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", "true"));
        connection.createAnswer(sdpObserver, sdpMediaConstraints);
    }
}
