package com.kotatu.android.chat;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Created by mayuhei on 2017/05/24.
 */

public class AudioRecordThread implements Runnable {
    private Callback callback;
    private boolean isAudioRecording;
    private static String LOG_TAG = AudioRecordThread.class.getCanonicalName();

    public AudioRecordThread(Callback callback){
        this.callback = callback;
    }

    public boolean isAudioRecording() {
        return isAudioRecording;
    }

    public void setAudioRecording(boolean audioRecording) {
        isAudioRecording = audioRecording;
    }

    @Override
    public void run() {
        int bufferLength = 512;
        int bufferSize;
        short[] audioData;
        int bufferReadResult;
        int sampleAudioBitRate = 44100;
        try {
            bufferSize = AudioRecord.getMinBufferSize(sampleAudioBitRate,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioBitRate,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferLength);
            audioData = new short[bufferLength];
            audioRecord.startRecording();
            Log.d(LOG_TAG, "audioRecord.startRecording()");
            isAudioRecording = true;
                /* ffmpeg_audio encoding loop */
            while (isAudioRecording) {
                bufferReadResult = audioRecord.read(audioData, 0, audioData.length);
                ShortBuffer realAudioData = ShortBuffer.wrap(audioData,0,bufferReadResult);
                callback.call(realAudioData);
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
        void call(ShortBuffer buffer);
    }
}
