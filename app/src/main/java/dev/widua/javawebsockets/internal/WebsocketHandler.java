package dev.widua.javawebsockets.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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

	@Override
	public void run() {
		System.out.println("Request is parsing");
		try {
			var req = parser.parse(input);
			System.out.println("Request parsed");
			if (req.getTarget().equals("/")) {
				ResponseSender.homePage(output);

			}
			if (!req.getTarget().equals("/ws")) {
				ResponseSender.defaultErrorResponse(output, "This project only support websocket");
			}
			System.out.println(req);


		} catch (IOException ex) {
			ResponseSender.defaultErrorResponse(output, ex.getMessage());
		}

	}
}
