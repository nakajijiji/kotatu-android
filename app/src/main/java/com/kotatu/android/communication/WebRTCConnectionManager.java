package com.kotatu.android.communication;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.communication.message.CallMe;
import com.kotatu.android.communication.message.IceCandidateMessage;
import com.kotatu.android.communication.message.Message;
import com.kotatu.android.communication.message.SdpMessage;
import com.kotatu.android.communication.observer.DefaultDataChannelObserver;
import com.kotatu.android.communication.observer.DefaultObserver;
import com.kotatu.android.communication.observer.DefaultSdpObserver;
import com.kotatu.android.communication.observer.DoNothingOnMessageCallback;
import com.kotatu.android.communication.observer.DoNothingSdpObserver;
import com.kotatu.android.util.JsonSerializer;

import org.webrtc.DataChannel;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mayuhei on 2017/05/17.
 */

public class WebRTCConnectionManager {
    public static String TAG = WebRTCConnectionManager.class.getCanonicalName();
    private Socket socket;
    private String roomId;
    private PeerConnectionFactory factory;
    private Map<String, PeerConnection> socketIdToPeerConnection = new HashMap<>();
    private Map<String, DataChannel> socketIdToDataChannel = new HashMap<>();
    private DefaultDataChannelObserver.OnMessageCallback callback;

    public WebRTCConnectionManager(PeerConnectionFactory factory, String roomId, DefaultDataChannelObserver.OnMessageCallback callback) {
        this.factory = factory;
        this.roomId = roomId;
        this.callback = callback;
    }

    public void connect(String signalingServerAddress, final List<PeerConnection.IceServer> iceServers) {
        connectIfDisconnected(roomId, signalingServerAddress, iceServers);
        CallMe callMe = new CallMe();
        callMe.setRoomId(roomId);
        socket.emit(SocketMessageKey.MESSAGE.toString(), JsonSerializer.serialize(callMe));
    }

    public void disconnect() {
        for(Map.Entry<String, PeerConnection> e : socketIdToPeerConnection.entrySet()){
            PeerConnection connection = e.getValue();
            connection.close();
        }
        socket.emit(SocketMessageKey.LEAVE.toString(), roomId);
        if (socket.connected()) {
            socket.disconnect();
        }
    }

    public void broadcast(ByteBuffer message){
        for(Map.Entry<String, DataChannel> e : socketIdToDataChannel.entrySet()){
            message.position(0);
            DataChannel channel = e.getValue();
            DataChannel.Buffer buffer = new DataChannel.Buffer(message, true);
            channel.send(buffer);
        }
    }

    private void connectIfDisconnected(final String roomId, String signalingServerAddress, final List<PeerConnection.IceServer> iceServers) {
        if (socket == null){
            try {
                IO.Options options = new IO.Options();
                options.forceNew = true;
                socket = IO.socket(signalingServerAddress, options);
            } catch (URISyntaxException e) {

            }
        }
        if (!socket.connected()) {
            socket.connect();
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
        }
        socket.emit(SocketMessageKey.JOIN.toString(), roomId);
    }

    private PeerConnection createPeerConnectionIfNotExists(List<PeerConnection.IceServer> iceServers, final String from){
        PeerConnection connection = socketIdToPeerConnection.get(from);
        if(connection == null) {
            PeerConnection.Observer observer = new DefaultObserver(socket, roomId, from, callback){
                @Override
                public void onDataChannel(DataChannel dataChannel){
                    dataChannel.registerObserver(new DefaultDataChannelObserver(from, dataChannel, callback));
                    socketIdToDataChannel.put(from+"remote", dataChannel);
                }
            };
            MediaConstraints constraints = new MediaConstraints();
            constraints.optional.add(new MediaConstraints.KeyValuePair("RtpDataChannels", "false"));
            connection = factory.createPeerConnection(iceServers, constraints, observer);
            Log.d(TAG + "iceee", connection.iceConnectionState().toString());

            socketIdToPeerConnection.put(from, connection);
           // MediaStreamFactory streamFactory = new AudioMediaStreamFactory(factory);
            //connection.addStream(streamFactory.create());

            // datachannelの設定
            DataChannel.Init channelOptions = new DataChannel.Init();
            channelOptions.ordered = false;
            channelOptions.maxRetransmits = 0;
            DataChannel dataChannel = connection.createDataChannel(from, channelOptions);
            //DefaultDataChannelObserver dataChannelObserver = new DefaultDataChannelObserver(from, dataChannel, callback);
            //dataChannel.registerObserver(dataChannelObserver);
            socketIdToDataChannel.put(from+"local", dataChannel);
        }
        return connection;
    }

    public static interface Callback {
        void call();
    }
}
