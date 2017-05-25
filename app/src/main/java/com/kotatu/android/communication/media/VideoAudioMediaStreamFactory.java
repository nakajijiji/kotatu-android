package com.kotatu.android.communication.media;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

/**
 * Created by mayuhei on 2017/05/17.
 */

public class VideoAudioMediaStreamFactory extends MediaStreamFactory {
    public VideoAudioMediaStreamFactory(PeerConnectionFactory factory) {
        super(factory);
    }

    @Override
    public MediaStream create() {
        VideoCapturer capturer = VideoCapturer.create(VideoCapturerAndroid.getNameOfFrontFacingDevice());
        VideoSource videoSource = factory.createVideoSource(capturer, defaultVideoConstraints());
        VideoTrack localVideoTrack = factory.createVideoTrack("dummy_video", videoSource);
        AudioSource audioSource = factory.createAudioSource(defaultAudioConstraints());
        AudioTrack localAudioTrack = factory.createAudioTrack("dummy_audio", audioSource);
        MediaStream mediaStream = factory.createLocalMediaStream("dummy_stream");
        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);
        return mediaStream;
    }

    private static MediaConstraints defaultVideoConstraints() {
        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", "1280"));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", "720"));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minWidth", "640"));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minHeight", "480"));
        return videoConstraints;
    }

    private static MediaConstraints defaultAudioConstraints() {
        MediaConstraints audioConstraints = new MediaConstraints();
        return audioConstraints;
    }
}
