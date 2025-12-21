package dev.widua.javawebsockets.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import dev.widua.javawebsockets.internal.request.RequestParser;
import dev.widua.javawebsockets.internal.response.ResponseSender;

public class WebsocketHandler implements Runnable {
    private InputStream input;
    private OutputStream output;
    private RequestParser parser;

    public WebsocketHandler(Socket connection) throws IOException {
        input = connection.getInputStream();
        output = connection.getOutputStream();
        this.parser = new RequestParser();
    }

    private void pingControlFrame() {
        try {
            output.write(new byte[]{
                    (byte) 0x89,0x00
			});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pongControlFrame() {
        try {
            output.write(new byte[]{
                    (byte) 0x8A,0x00
			});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeControlFrame() {
        try {
            output.write(new byte[]{
                    (byte) 0x88,0x00
			});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listenOnWebsockets() throws IOException{

        while (true) {

            var frameBuf = input.readNBytes(2);

            System.out.println(frameBuf[0]);

            var frameType = frameBuf[0] & 0x0F;
            var dataLength = frameBuf[1] & 0x0F;

            switch (frameType) {
                case 0x1 -> {
                    System.out.println();
                }
                case 0x2 -> {

                }
                case 0x8 -> {
                    return;
                }
                case 0x9 -> {
                    pongControlFrame();
                }
            }

        }

    }

    @Override
    public void run() {
        System.out.println("Request is parsing");
        try {
            var req = parser.parse(input);
            System.out.println("Request parsed");
            if (req.getTarget().equals("/")) {
                ResponseSender.homePage(output);
            }
			if (req.getTarget().equals("/ws")) {
			ResponseSender.websocketHandshake(output, req);
            listenOnWebsockets();
			closeControlFrame();
			}


        } catch (IOException ex) {
            ResponseSender.defaultErrorResponse(output, ex.getMessage());
        }

    }
}
