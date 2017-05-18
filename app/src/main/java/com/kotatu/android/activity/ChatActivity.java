package com.kotatu.android.activity;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.R;
import com.kotatu.android.chat.observer.DefaultObserver;
import com.kotatu.android.chat.ConnectionManager;

import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


public class ChatActivity extends Activity {

    private final String TAG = "sample";
    private final String STRN = "stun:stun.l.google.com:19302";
    private final String SIGNALING = "http://ec2-52-198-242-194.ap-northeast-1.compute.amazonaws.com:3000";
    private final String DUMMY_ROOM_ID = "1";

    private Socket socket;
    {
        try {
            socket = IO.socket(SIGNALING);
        } catch (URISyntaxException e) {

        }
    }
    private PeerConnectionFactory factory;
    private String currentRoomId;
    private ConnectionManager connectionManager;
    private VideoRenderer renderer;

    public void onClick(View view) {
        if(connectionManager == null) {
            enter();
        }else{
            leave();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currentRoomId = DUMMY_ROOM_ID;
        setContentView(R.layout.activity_main);
        initPeerConnectionFactory();
        this.renderer = createVideoRenderer();
        final PeerConnection.Observer observer = new DefaultObserver(socket);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connectionManager == null){
            return;
        }
        connectionManager.disconnect(new ConnectionManager.Callback() {
            @Override
            public void call() {

            }
        });
    }

    private synchronized void enter(){
        final ConnectionManager connectionManager = new ConnectionManager(factory, new DefaultObserver(socket), socket, currentRoomId);
        this.connectionManager = connectionManager;
        final Button button = (Button) findViewById(R.id.button);
        connectionManager.connect(getIceServers(), new ConnectionManager.Callback() {
            @Override
            public void call() {
                button.setText("Leave");
            }
        });
    }

    private synchronized void leave(){
        if(connectionManager == null){
            return;
        }
        final Button button = (Button) findViewById(R.id.button);
        connectionManager.disconnect(new ConnectionManager.Callback() {
            @Override
            public void call() {
                button.setText("Enter Room");
            }
        });
        connectionManager = null;
    }

    private List<PeerConnection.IceServer> getIceServers(){
        List<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();
        iceServers.add(new PeerConnection.IceServer(STRN));
        return iceServers;
    }

    private void initPeerConnectionFactory(){
        PeerConnectionFactory.initializeAndroidGlobals(getApplicationContext(), true, true, true, null);
        PeerConnectionFactory factory = new PeerConnectionFactory();
        this.factory = factory;
    }

    private VideoRenderer createVideoRenderer(){
        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.surfaceviewclass);
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
                // Do Nothing
            }
        });
        try {
            return VideoRendererGui.createGui(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
