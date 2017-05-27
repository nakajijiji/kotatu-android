package com.kotatu.android.communication.observer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import org.webrtc.DataChannel;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by mayuhei on 2017/05/25.
 */

public class PlayAudio implements DefaultDataChannelObserver.OnMessageCallback{
    private static String TAG = PlayAudio.class.getCanonicalName();
    private Map<String, AudioPlayTask> taskMap = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public void call(String socketId, DataChannel.Buffer buffer) {
        AudioPlayTask task = taskMap.get(socketId);
        if(task == null){
            task = new AudioPlayTask();
            taskMap.put(socketId, task);
            task.audioPlay = true;
            task.socketId = socketId;
            task.bufferQueue = new LinkedBlockingQueue<>(50);
            executor.submit(task);
            taskMap.put(socketId, task);
        }
        task.bufferQueue.offer(buffer);
    }

    @Override
    public void destroy() {
        for(Map.Entry<String, AudioPlayTask> e : taskMap.entrySet()){
            e.getValue().audioPlay = false;
        }
        executor.shutdown();
    }

    private class AudioPlayTask implements Runnable{
        public boolean audioPlay = false;
        public String socketId;
        public LinkedBlockingQueue<DataChannel.Buffer> bufferQueue;

        @Override
        public void run() {
            Log.d(TAG, "start audio");
            AudioTrack track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,4096, AudioTrack.MODE_STREAM );
            track.play();
            while(audioPlay){
                Log.d(TAG, "" + bufferQueue.size());
                DataChannel.Buffer buffer = null;
                try {
                    buffer = bufferQueue.poll(300l, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new IllegalStateException("interrupted");
                }
                if(buffer == null){
                    continue;
                }
                ByteBuffer result = buffer.data;
                track.write(result, result.capacity(), AudioTrack.WRITE_BLOCKING);
            }
            track.release();
        }
    }

    /*
    https://chromium.googlesource.com/external/webrtc/+/master/webrtc/modules/audio_device/android/java/src/org/webrtc/voiceengine/WebRtcAudioTrack.java
    が参考になるかも
     */
}
