package com.kotatu.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.kotatu.android.R;
import com.kotatu.android.chat.AudioRecordThread;
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
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ChatRoomActivity extends Activity implements
        GestureDetector.OnGestureListener {

    private final String TAG = "sample";
    private final String STRN = "stun:stun.l.google.com:19302";
    // X軸最低スワイプ距離
    private static final int SWIPE_MIN_DISTANCE = 50;

    // X軸最低スワイプスピード
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    // Y軸の移動距離　これ以上なら横移動を判定しない
    private static final int SWIPE_MAX_OFF_PATH = 250;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private PeerConnectionFactory factory;
    private String currentRoomId;
    private ConnectionManager connectionManager;
    private GestureDetectorCompat mDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        float distance_x = Math.abs((event1.getX() - event2.getX()));
        float velocity_x = Math.abs(velocityX);

        // Y軸の移動距離が大きすぎる場合
        if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) {
            //Do Nothing
        } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            finish();
        } else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            Intent intent = new Intent(ChatRoomActivity.this, VRChatActivity.class);
            intent.putExtra(IntentKey.ROOM_ID, currentRoomId);
            startActivity(intent);
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetectorCompat(this, this);
        Intent intent = getIntent();
        String roomString = intent.getStringExtra(IntentKey.ROOM);
        Room room = JsonSerializer.deserialize(roomString, Room.class);
        this.currentRoomId = room.getRoomId();
        setContentView(R.layout.chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(room.getName());
        ListView view = (ListView) findViewById(R.id.member_list);
        view.setAdapter(new DefaultUserListAdapter(getApplicationContext(), room.getMembers()));
        initPeerConnectionFactory();
        enter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionManager == null) {
            return;
        }
        connectionManager.disconnect(new ConnectionManager.Callback() {
            @Override
            public void call() {
                executor.shutdown();
            }
        });
    }

    private synchronized void enter() {
        final ConnectionManager connectionManager = new ConnectionManager(factory, currentRoomId);
        this.connectionManager = connectionManager;
        connectionManager.connect(getIceServers(), new ConnectionManager.Callback() {
            @Override
            public void call() {
                TextView view = (TextView) findViewById(R.id.connection_status);
                view.setText(R.string.connected);
                //button.setText("Leave");
            }
        });
        executor.submit(new AudioRecordThread(new AudioRecordThread.Callback() {
            @Override
            public void call(ShortBuffer buffer) {
                ByteBuffer bb = ByteBuffer.allocateDirect(buffer.array().length * 2);
                for (short s : buffer.array()) {
                    bb.putShort(s);
                }
                connectionManager.broadcast(bb);
            }
        }));
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

    private List<PeerConnection.IceServer> getIceServers() {
        List<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();
        iceServers.add(new PeerConnection.IceServer(STRN));
        return iceServers;
    }

    private void initPeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(getApplicationContext(), true, true, true, null);
        PeerConnectionFactory factory = new PeerConnectionFactory();
        this.factory = factory;
    }
}
