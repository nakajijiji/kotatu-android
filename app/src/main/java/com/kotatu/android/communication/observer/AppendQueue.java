package com.kotatu.android.communication.observer;

import com.kotatu.android.communication.observer.DefaultDataChannelObserver;

import org.webrtc.DataChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mayuhei on 2017/05/25.
 */

public class AppendQueue implements DefaultDataChannelObserver.OnMessageCallback{
    private Map<String, Queue<DataChannel.Buffer>> realtimeActionMap = new HashMap<>();

    public AppendQueue(Map<String, Queue<DataChannel.Buffer>> queue){
        this.realtimeActionMap = queue;
    }

    @Override
    public void call(String socketId, DataChannel.Buffer buffer) {
        Queue<DataChannel.Buffer> queue = realtimeActionMap.get(socketId);
        if(queue == null){
            queue = new LinkedBlockingQueue<>();
            realtimeActionMap.put(socketId, queue);
        }
        queue.add(buffer);
    }

    @Override
    public void destroy() {
        realtimeActionMap = new HashMap<>();
    }
}
