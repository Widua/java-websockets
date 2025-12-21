package dev.widua.javawebsockets.internal.response;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import dev.widua.javawebsockets.internal.headers.Headers;
import dev.widua.javawebsockets.internal.request.HttpRequest;

public class ResponseSender {
	private static final String protocol = "HTTP/1.1";
	private static final String websocketGUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

	public static void defaultErrorResponse(OutputStream out, String errorMessage) {
		try {
			writeStatusLine(out, 400, "Bad Request");
			var headers = getDefaultHeaders(errorMessage.length());
			headers.write(out);
			out.write(errorMessage.getBytes());
		} catch (IOException ignored) {
		}
	}

	public static void homePage(OutputStream out) {

        try {
            var data = Files.readAllBytes(Path.of("resources/static/index.html"));
			writeStatusLine(out,200,"OK");
			var headers = getDefaultHeaders(data.length);
			headers.put("Content-Type","text/html");
			headers.write(out);
			out.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

	public static void websocketHandshake(OutputStream out, HttpRequest request) {
		var headers = request.getHeaders();

		var connection = headers.get("Connection");
		if (!connection.contains("Upgrade")){
			defaultErrorResponse(out,"Need Connection: Upgrade header");
		}
		var upgrade = headers.get("Upgrade");
		if (!upgrade.equals("websocket")) {
			defaultErrorResponse(out,"This project support only websockets");
		}

		var wsKey = headers.get("Sec-websocket-key");
		if (wsKey.isEmpty()) {
			defaultErrorResponse(out,"Connection needs an key");
		}

		try {
			writeStatusLine(out,101,"Switching Protocols");
			var resHeaders = getWebsocketHandshakeHeaders(wsKey);
			resHeaders.write(out);

		} catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

	public static String createWebsocketResponseKey(String wsKey){
		try {
		var sha1md = MessageDigest.getInstance("SHA-1");
		wsKey = wsKey.trim();

		var joinedKey = wsKey + websocketGUID;
		sha1md.reset();
		sha1md.update(joinedKey.getBytes());
		var sha1EncryptedKey = sha1md.digest();
            return Base64.getEncoder().encodeToString(sha1EncryptedKey);
		} catch (NoSuchAlgorithmException algorithmException){
			throw new RuntimeException("The algorithm SHA-1(specialized in RFC6455) for WS is not supported in server");
        }
	}

	private static Headers getDefaultHeaders(int contentLength) {
		var headers = new Headers();
		headers.put("Content-Type", "text/plain");
		headers.put("Content-Length", String.valueOf(contentLength));
		headers.put("Connection", "close");
		return headers;
	}

	private static Headers getWebsocketHandshakeHeaders(String websocketKey){
		var headers = new Headers();
		headers.put("Upgrade","websocket");
		headers.put("Connection","Upgrade");
		headers.put("Sec-WebSocket-Accept",createWebsocketResponseKey(websocketKey.trim()));

		return headers;
	}

	private static void writeStatusLine(OutputStream out, int statusCode, String reason) throws IOException {
		var statusLine = String.format("%s %s %s\r\n", protocol, statusCode, reason);
		out.write(statusLine.getBytes());
	}

}
