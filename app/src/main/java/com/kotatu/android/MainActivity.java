package com.kotatu.android;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.chat.DefaultObserver;
import com.kotatu.android.chat.MediaStreamFactory;
import com.kotatu.android.chat.ConnectionManager;
import com.kotatu.android.chat.VideoAudioMediaStreamFactory;

import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends Activity {

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
    private ConnectionManager connectionManager;
    private PeerConnection connection;

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
            return VideoRendererGui.createGui((int)videoView.getX(), (int)videoView.getY(), videoView.getWidth(), videoView.getHeight(), VideoRendererGui.ScalingType.SCALE_FILL, false);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPeerConnectionFactory();
        VideoRenderer renderer = createVideoRenderer();
        final PeerConnection.Observer observer = new DefaultObserver(socket, renderer);
        this.connectionManager = new ConnectionManager(factory, observer, socket);
    }


    @Override
    protected void onResume() {
        super.onResume();
        List<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();
        iceServers.add(new PeerConnection.IceServer(STRN));
        connectionManager.reconnect(iceServers, DUMMY_ROOM_ID);
        MediaStreamFactory streamFactory = new VideoAudioMediaStreamFactory(factory);
        streamFactory.create();
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionManager.disConnect();
    }

    public void onClick(View view) {
        connectionManager.offer();
    }
}
