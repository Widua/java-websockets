package dev.widua.javawebsockets.internal;

import dev.widua.javawebsockets.internal.request.RequestParser;
import dev.widua.javawebsockets.internal.response.ResponseSender;
import dev.widua.javawebsockets.internal.response.WebsocketResponseSender;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.UUID;

public class WebsocketHandler implements Runnable {
    private final InputStream input;
    private final OutputStream output;
    private final RequestParser parser;
    private final WebsocketResponseSender websocketResponse;

    public WebsocketHandler(Socket connection) throws IOException {
        this.input = connection.getInputStream();
        this.output = connection.getOutputStream();
        this.parser = new RequestParser();
        this.websocketResponse = new WebsocketResponseSender(output);
    }

    private void listenOnWebsockets() throws IOException {

        while (true) {
            var frameBuf = input.readNBytes(2);

            var frameType = frameBuf[0] & 0x0F;
            long dataLength = frameBuf[1] & 0x7F;

            if (dataLength == 127) {
                var extendedLen = input.readNBytes(8);
                ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
                bb.put(extendedLen);
                dataLength = bb.getShort();
            }
            if (dataLength == 126) {
                var extendedLen = input.readNBytes(2);
                ByteBuffer bb = ByteBuffer.allocate(Short.BYTES);
                bb.put(extendedLen);
                dataLength = bb.getLong();
            }
            var masked = ((frameBuf[1] & 0x80)) != 0;
            var maskKey = new byte[4];

            if (masked) {
                maskKey = input.readNBytes(4);
            }
            var payloadData = input.readNBytes((int) dataLength);

            for (int i = 0; i < payloadData.length; i++) {
                payloadData[i] = (byte) (payloadData[i] ^ maskKey[i % 4]);
            }

            switch (frameType) {
                case 0x1 -> {
                    System.out.println("Data: " + new String(payloadData));
                    websocketResponse.sendMessage("Message received", true);
                }
                case 0x2 -> {
                    var fname = saveBinary(payloadData);
                    System.out.println("Binary saved to file: " + fname);

                }
                case 0x8 -> {
                    websocketResponse.closeControlFrame();
                    return;
                }
                case 0x9 -> {
                    websocketResponse.closeControlFrame();
                }
            }

        }

    }

    private String saveBinary(byte[] binary) throws IOException {
        String fname = UUID.randomUUID() + ".png";
        var file = new File(fname);
        file.createNewFile();

        var fos = new FileOutputStream(file);
        fos.write(binary);
        fos.flush();
        fos.close();
        return fname;
    }

    @Override
    public void run() {
        System.out.println("Request is parsing");
        try {
            var req = parser.parse(input);
            System.out.println("Request parsed");
            if (!req.getMethod().equalsIgnoreCase("GET")) {
                ResponseSender.defaultErrorResponse(output, "Only GET is supported");
            }
            if (req.getTarget().equals("/")) {
                ResponseSender.homePage(output);
            }
            if (req.getTarget().equals("/ws")) {
                ResponseSender.websocketHandshake(output, req);
                listenOnWebsockets();
            }

        } catch (IOException ex) {
            ResponseSender.defaultErrorResponse(output, ex.getMessage());
        }

    }
}
