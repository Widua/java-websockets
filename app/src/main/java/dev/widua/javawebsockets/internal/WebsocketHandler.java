package dev.widua.javawebsockets.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WebsocketHandler implements Runnable {
	private InputStream input;
	private OutputStream output;

	public WebsocketHandler(Socket connection) throws IOException {
		input = connection.getInputStream();
		output = connection.getOutputStream();
	}

	@Override
	public void run() {
		System.out.println("Request is parsing");
	}
}
