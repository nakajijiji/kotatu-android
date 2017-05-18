package com.kotatu.android.chat;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.chat.message.CallMe;
import com.kotatu.android.chat.message.IceCandidateMessage;
import com.kotatu.android.chat.message.Message;
import com.kotatu.android.chat.message.SdpMessage;
import com.kotatu.android.util.JsonSerializer;

import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mayuhei on 2017/05/17.
 */

public class ConnectionManager {
    public static String TAG = ConnectionManager.class.getCanonicalName();

    private PeerConnectionFactory factory;
    private Map<String, PeerConnection> socketIdToPeerConnection = new HashMap<>();
    private PeerConnection.Observer observer;
    private Socket socket;
    private String roomId;

    public ConnectionManager(PeerConnectionFactory factory, PeerConnection.Observer observer, Socket socket, String roomId) {
        this.factory = factory;
        this.socket = socket;
        this.observer = observer;
        this.roomId = roomId;
    }

    public void connect(final List<PeerConnection.IceServer> iceServers, Callback callback) {
        connectIfDisconnected(roomId);
        socket.on(SocketMessageKey.MESSAGE.toString(), new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(args.length != 1){
                    Log.d(TAG, "illegal message");
                    return;
                }
                String messageString = args[0].toString();
                Message message = JsonSerializer.deserialize(messageString, Message.class);
                if(!message.getRoomId().equals(roomId)){
                    return;
                }
                PeerConnection connection = createPeerConnectionIfNotExists(iceServers, message.getFrom());
                switch (message.getType()) {
                    case CALLME:
                        Log.d(TAG, "Recive CALLME");
                        message = JsonSerializer.deserialize(messageString, CallMe.class);
                        connection.createOffer(new DefaultSdpObserver(connection, socket, message), new DefaultMediaConstraints());
                        break;
                    case OFFER:
                        message = JsonSerializer.deserialize(messageString, SdpMessage.class);
                        Log.d(TAG, "Recive OFFER");
                        connection.setRemoteDescription(new DefaultSdpObserver(connection, socket, message).answerMode(true), ((SdpMessage) message).getSessionDescription());
                        break;
                    case ANSWER:
                        Log.d(TAG, "Recive ANSWER");
                        message = JsonSerializer.deserialize(messageString, SdpMessage.class);
                        connection.setRemoteDescription(new DoNothingSdpObserver(), ((SdpMessage) message).getSessionDescription());
                        break;
                    case ICE_CANDIDATE:
                        Log.d(TAG, "Recive ICE_CANDIDATE");
                        message = JsonSerializer.deserialize(messageString, IceCandidateMessage.class);
                        connection.addIceCandidate(((IceCandidateMessage)message).getIceCandidate());
                        break;
                }
            }
        });
        CallMe callMe = new CallMe();
        callMe.setRoomId(roomId);
        socket.emit(SocketMessageKey.MESSAGE.toString(), JsonSerializer.serialize(callMe));
        callback.call();
    }

    public void disconnect(Callback callback) {
        for(Map.Entry<String, PeerConnection> e : socketIdToPeerConnection.entrySet()){
            PeerConnection connection = e.getValue();
            connection.close();
        }
        socket.emit(SocketMessageKey.LEAVE.toString(), roomId);
        if (socket.connected()) {
            socket.disconnect();
        }
        callback.call();
    }

    private void connectIfDisconnected(String roomId) {
        if (!socket.connected()) {
            socket.connect();
        }
        socket.emit(SocketMessageKey.JOIN.toString(), roomId);
    }

    private PeerConnection createPeerConnectionIfNotExists(List<PeerConnection.IceServer> iceServers, String from){
        PeerConnection connection = socketIdToPeerConnection.get(from);
        if(connection == null) {
            connection = factory.createPeerConnection(iceServers, new MediaConstraints(), observer);
            socketIdToPeerConnection.put(from, connection);
            MediaStreamFactory streamFactory = new VideoAudioMediaStreamFactory(factory, null);
            connection.addStream(streamFactory.create());
        }
        return connection;
    }

    public static interface Callback {
        void call();
    }
}
