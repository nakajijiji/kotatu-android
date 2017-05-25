package com.kotatu.android.communication.media;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class AudioMediaStreamFactory extends MediaStreamFactory {
    public AudioMediaStreamFactory(PeerConnectionFactory factory) {
        super(factory);
    }

    @Override
    public MediaStream create() {
        AudioSource audioSource = factory.createAudioSource(defaultAudioConstraints());
        AudioTrack localAudioTrack = factory.createAudioTrack("dummy_audio", audioSource);
        MediaStream mediaStream = factory.createLocalMediaStream("dummy_stream");
        mediaStream.addTrack(localAudioTrack);
        return mediaStream;
    }

    private static MediaConstraints defaultAudioConstraints() {
        MediaConstraints audioConstraints = new MediaConstraints();
        return audioConstraints;
    }
}
