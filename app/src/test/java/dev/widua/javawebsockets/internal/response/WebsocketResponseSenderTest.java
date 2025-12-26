package dev.widua.javawebsockets.internal.response;

import dev.widua.javawebsockets.internal.headers.WebSocketsOpCode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WebsocketResponseSenderTest {

    @Test
    public void testHeaderFrameSmallSize() {
        var sender = new WebsocketResponseSender(null);

        var output = sender.getDefaultHeaderFrame(16, true, WebSocketsOpCode.TEXT);
        var output2 = sender.getDefaultHeaderFrame(16, false, WebSocketsOpCode.TEXT);

        var frameType = output[0] & 0x0f;
        var fin = output[0] & 0x80;
        var rsv = output[0] & 0x70;
        var masked = (output[1] & 0x80) != 0;
        var masked2 = (output2[1] & 0x80) != 0;
        var length = output[1] & 0x7F;


        assertEquals(2, output.length);

        assertNotEquals(0, frameType);
        assertEquals(0, rsv);
        assertNotEquals(0, fin);
        assertTrue(masked);
        assertFalse(masked2);
        assertEquals(16, length);
    }

    @Test
    public void testHeaderFrameShortSize() {
        var sender = new WebsocketResponseSender(null);

        var output = sender.getDefaultHeaderFrame(200, true, WebSocketsOpCode.TEXT);
        var output2 = sender.getDefaultHeaderFrame(200, false, WebSocketsOpCode.TEXT);

        var frameType = output[0] & 0x0f;
        var fin = output[0] & 0x80;
        var rsv = output[0] & 0x70;
        var masked = (output[1] & 0x80) != 0;
        var masked2 = (output2[1] & 0x80) != 0;

        var length = Arrays.copyOfRange(output, 2, 4);

        assertEquals(4, output.length);

        assertNotEquals(0, frameType);
        assertEquals(0, rsv);
        assertNotEquals(0, fin);
        assertTrue(masked);
        assertFalse(masked2);
        assertArrayEquals(new byte[]{0x00, (byte) 0xC8}, length);
    }

    @Test
    public void testHeaderFrameLargeSize() {
        var sender = new WebsocketResponseSender(null);

        var output = sender.getDefaultHeaderFrame(40_000, true, WebSocketsOpCode.TEXT);
        var output2 = sender.getDefaultHeaderFrame(40_000, false, WebSocketsOpCode.TEXT);

        var frameType = output[0] & 0x0f;
        var fin = output[0] & 0x80;
        var rsv = output[0] & 0x70;
        var masked = (output[1] & 0x80) != 0;
        var masked2 = (output2[1] & 0x80) != 0;

        var length = Arrays.copyOfRange(output, 2, 10);

        assertEquals(10, output.length);

        assertNotEquals(0, frameType);
        assertEquals(0, rsv);
        assertNotEquals(0, fin);
        assertTrue(masked);
        assertFalse(masked2);
        assertArrayEquals(new byte[]{0x00, (byte) 0x9C, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00}, length);
    }

}