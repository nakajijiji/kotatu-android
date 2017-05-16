package com.kotatu.android.chat;

import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.util.JsonSerializer;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

/**
 * Created by mayuhei on 2017/05/15.
 */

public class DefaultSdpObserver implements SdpObserver{
    private final String TAG = "sample";

    private PeerConnection connection;
    private Socket socket;

    public DefaultSdpObserver(PeerConnection connection, Socket socket){
        super();
        this.connection = connection;
        this.socket = socket;
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        Log.d(TAG, "so1 onCreateSuccess()");
        connection.setLocalDescription(this, sessionDescription);
        SdpMessage message = new SdpMessage();
        message.setSessionDescription(sessionDescription);
        message.setRoomId("1");
        socket.emit(SocketMessageKey.SEND_SDP.toString(), JsonSerializer.serialize(message));
    }

    @Override
    public void onSetSuccess() {
        Log.d(TAG, "so1 onSetSuccess()");
    }

    @Override
    public void onCreateFailure(String s) {
        Log.d(TAG, "so1 onCreateFailure() " + s);
    }

    @Override
    public void onSetFailure(String s) {
        Log.d(TAG, "so1 onSetFailure() " + s);
    }
}
