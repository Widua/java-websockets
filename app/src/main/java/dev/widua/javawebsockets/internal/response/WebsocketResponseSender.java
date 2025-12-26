package dev.widua.javawebsockets.internal.response;

import dev.widua.javawebsockets.internal.headers.WebSocketsOpCode;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class WebsocketResponseSender {
    private final OutputStream output;
    private final byte[] wsMask = new byte[]{
            (byte) 0xAA, (byte) 0xAB, 0x77, (byte) 0xAA
    };

    public WebsocketResponseSender(OutputStream output) {
        this.output = output;
    }

    public void pingControlFrame() {
        try {
            output.write(new byte[]{
                    (byte) 0x89, 0x00
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void pongControlFrame() {
        try {
            output.write(new byte[]{
                    (byte) 0x8A, 0x00
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeControlFrame() {
        try {
            output.write(new byte[]{
                    (byte) 0x88, 0x00
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getDefaultHeaderFrame(int byteLong, boolean masked, WebSocketsOpCode opcode) {

        var finrsvopcode = opcode.getOpcode(true);
        var maskByte = masked ? 0x80 : 0x00;
        if (byteLong < 126) {
            var frameHeader = new byte[2];
            frameHeader[0] = finrsvopcode;
            frameHeader[1] = (byte) (maskByte | (byte) byteLong);
            return frameHeader;
        }
        if (byteLong <= Short.MAX_VALUE) {
            var frameHeader = new byte[4];
            frameHeader[0] = finrsvopcode;
            frameHeader[1] = (byte) (maskByte | (byte) 0x7E);

            var ix = 2;

            var trueLen = BigInteger.valueOf(byteLong).toByteArray();

            for (byte b : trueLen) {
                frameHeader[ix] = b;
                ix++;
            }

            return frameHeader;
        }

        var frameHeader = new byte[10];
        frameHeader[0] = finrsvopcode;
        frameHeader[1] = (byte) ((byte) maskByte | 0x7F);

        var ix = 2;

        var trueLen = BigInteger.valueOf(byteLong).toByteArray();

        for (byte b : trueLen) {
            frameHeader[ix] = b;
            ix++;
        }

        return frameHeader;
    }

    public byte[] maskMessage(byte[] message) {

        for (int i = 0; i < message.length; i++) {
            message[i] = (byte) (message[i] ^ wsMask[i % 4]);
        }
        return message;
    }

    public void sendMessage(String message, boolean mask) throws IOException {
        var messageBytes = message.getBytes();
        var messageSize = messageBytes.length;
        output.write(getDefaultHeaderFrame(messageSize, mask, WebSocketsOpCode.TEXT));
        if (mask) {
            output.write(wsMask);
            output.write(maskMessage(messageBytes));
            return;
        }
        output.write(messageBytes);

    }

    public void sendBinary(byte[] message, boolean mask) throws IOException {
        var messageSize = message.length;

        output.write(getDefaultHeaderFrame(messageSize, mask, WebSocketsOpCode.BINARY));
        if (mask) {
            output.write(wsMask);
            output.write(maskMessage(message));
            return;
        }
        output.write(message);
    }

}
