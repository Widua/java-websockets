package dev.widua.javawebsockets.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class WebsocketHandler implements Runnable {
	private BufferedReader input;
	private BufferedWriter output;

	public WebsocketHandler(Socket connection) throws IOException {
		input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
	}

	@Override
	public void run() {
		System.out.println("Request is parsing");
	}
}
