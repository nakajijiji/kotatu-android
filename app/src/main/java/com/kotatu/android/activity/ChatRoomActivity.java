package com.kotatu.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.R;
import com.kotatu.android.chat.DefaultUserListAdapter;
import com.kotatu.android.chat.observer.DefaultObserver;
import com.kotatu.android.chat.ConnectionManager;
import com.kotatu.android.entity.Room;
import com.kotatu.android.intent.IntentKey;
import com.kotatu.android.util.JsonSerializer;

import org.w3c.dom.Text;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChatRoomActivity extends Activity {

    private final String TAG = "sample";
    private final String STRN = "stun:stun.l.google.com:19302";
    private final String SIGNALING = "http://ec2-52-198-242-194.ap-northeast-1.compute.amazonaws.com:3000";
    private final String DUMMY_ROOM_ID = "1";
    private ExecutorService executor = Executors.newSingleThreadExecutor();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String roomString = intent.getStringExtra(IntentKey.ROOM);
        Room room = JsonSerializer.deserialize(roomString, Room.class);
        this.currentRoomId = room.getRoomId();
        setContentView(R.layout.chat_room);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setSubtitle(room.getName());
        ListView view = (ListView)findViewById(R.id.member_list);
        view.setAdapter(new DefaultUserListAdapter(getApplicationContext(), room.getMembers()));
        initPeerConnectionFactory();
        executor.submit(new Runnable(){
            @Override
            public void run() {
                enter();
            }
        });
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
        connectionManager.connect(getIceServers(), new ConnectionManager.Callback() {
            @Override
            public void call() {
                TextView view = (TextView)findViewById(R.id.connection_status);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //Do Nothing
                }
                view.setText(R.string.connected);
                //button.setText("Leave");
            }
        });
    }

//    private synchronized void leave(){
//        if(connectionManager == null){
//            return;
//        }
//        final Button button = (Button) findViewById(R.id.button);
//        connectionManager.disconnect(new ConnectionManager.Callback() {
//            @Override
//            public void call() {
//                button.setText("Enter Lounge");
//            }
//        });
//        connectionManager = null;
//    }

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
}
