package com.kotatu.android.chat;

import com.kotatu.android.chat.message.SdpMessage;
import com.kotatu.android.util.JsonSerializer;

import org.junit.Test;
import org.webrtc.SessionDescription;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SdpMessageTest {
    @Test
    public void testFrom() throws Exception {
        SessionDescription description = new SessionDescription(SessionDescription.Type.ANSWER, "hoge");
        SdpMessage message = new SdpMessage();
        message.setSessionDescription(description);
        message.setRoomId("1");
        String str = JsonSerializer.serialize(message);
        SdpMessage result = JsonSerializer.deserialize(str, SdpMessage.class);
        assertEquals(SessionDescription.Type.ANSWER, result.getSessionDescription().type);
        assertEquals("hoge", result.getSessionDescription().description);
        assertEquals("1", result.getRoomId());
    }
}