package dev.widua.javawebsockets;

import java.io.IOException;
import java.net.ServerSocket;

import dev.widua.javawebsockets.internal.WebsocketHandler;

public class App {
	public static void main(String[] args) {
		try {
			var serverSocket = new ServerSocket(42069);
			serverSocket.setReuseAddress(true);
			while (true) {
				var client = serverSocket.accept();
				var websocketHandler = new WebsocketHandler(client);
				Thread.ofVirtual().start(websocketHandler);
			}
		} catch (IOException ex) {
			System.out.println("IO Exception: " + ex);
		}
	}
}
