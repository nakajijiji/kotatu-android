package com.kotatu.android.chat;

import com.kotatu.android.chat.message.IceCandidateMessage;
import com.kotatu.android.util.JsonSerializer;

import org.junit.Test;
import org.webrtc.IceCandidate;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class IceCandidateMessageTest {
    @Test
    public void testFrom() throws Exception {
        IceCandidate candidate = new IceCandidate("a", 1, "b");
        IceCandidateMessage message = new IceCandidateMessage();
        message.setIceCandidate(candidate);
        message.setRoomId("1");
        String str = JsonSerializer.serialize(message);
        IceCandidateMessage result = JsonSerializer.deserialize(str, IceCandidateMessage.class);
        assertEquals("a", result.getIceCandidate().sdpMid);
        assertEquals("b", result.getIceCandidate().sdp);
        assertEquals(1, result.getIceCandidate().sdpMLineIndex);
        assertEquals("1", result.getRoomId());
    }
}