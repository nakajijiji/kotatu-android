package com.kotatu.android.communication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Created by mayuhei on 2017/05/24.
 */

public class AudioRecordTask implements Runnable {
    private Callback callback;
    private boolean audioRecordEnabled;
    private static String LOG_TAG = AudioRecordTask.class.getCanonicalName();

    public AudioRecordTask(Callback callback){
        this.callback = callback;
    }

    public boolean isAudioRecordEnabled() {
        return audioRecordEnabled;
    }

    public void setAudioRecordEnabled(boolean audioRecordEnabled) {
        this.audioRecordEnabled = audioRecordEnabled;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
        int bufferLength = 960;
        short[] audioData;
        int bufferReadResult;
        int sampleAudioBitRate = 44100;
        try {
//            bufferSize = AudioRecord.getMinBufferSize(sampleAudioBitRate,
//                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioBitRate,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferLength);
            audioData = new short[bufferLength];
            audioRecord.startRecording();
            Log.d(LOG_TAG, "audioRecord.startRecording()");
            audioRecordEnabled = true;
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferLength);
            while (audioRecordEnabled) {
                bufferReadResult = audioRecord.read(byteBuffer, byteBuffer.capacity());
                callback.call(byteBuffer);
            }

                /* encoding finish, release recorder */
            if (audioRecord != null) {
                try {
                    audioRecord.stop();
                    audioRecord.release();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                audioRecord = null;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "get audio data failed:"+e.getMessage()+e.getCause()+e.toString());
        }

    }

    public static interface Callback{
        void call(ByteBuffer buffer);
    }
}
