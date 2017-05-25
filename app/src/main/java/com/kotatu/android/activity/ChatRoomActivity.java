package com.kotatu.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

import com.kotatu.android.R;
import com.kotatu.android.communication.CommunicationManager;
import com.kotatu.android.communication.DefaultCommunicationManager;
import com.kotatu.android.view.DefaultUserListAdapter;
import com.kotatu.android.entity.Room;
import com.kotatu.android.intent.IntentKey;
import com.kotatu.android.util.JsonSerializer;

import org.webrtc.PeerConnectionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChatRoomActivity extends Activity implements
        GestureDetector.OnGestureListener {

    private static final String TAG = "sample";
    private static final String SIGNALING = "http://ec2-52-198-242-194.ap-northeast-1.compute.amazonaws.com:3000";
    private static final String[] STRN = new String[]{"stun:stun.l.google.com:19302"};
    // X軸最低スワイプ距離
    private static final int SWIPE_MIN_DISTANCE = 50;

    // X軸最低スワイプスピード
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    // Y軸の移動距離　これ以上なら横移動を判定しない
    private static final int SWIPE_MAX_OFF_PATH = 250;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private PeerConnectionFactory factory;
    private String currentRoomId;
    private CommunicationManager communicationManager;
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
        CommunicationManager communicationManager = new DefaultCommunicationManager();
        communicationManager.init(getApplicationContext());
        this.communicationManager = communicationManager;
        enter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (communicationManager == null) {
            return;
        }
        communicationManager.disconnect(currentRoomId);
    }

    private synchronized void enter() {
        communicationManager.connect(currentRoomId, SIGNALING, STRN);
    }
}
