package dev.widua.javawebsockets.internal.response;

import java.io.IOException;
import java.io.OutputStream;

import dev.widua.javawebsockets.internal.headers.Headers;

public class ResponseSender {
	private static final String protocol = "HTTP/1.1";

	public static void defaultErrorResponse(OutputStream out, String errorMessage) {
		try {
			writeStatusLine(out, 400, "Bad Request");
			var headers = getDefaultHeaders(errorMessage.length());
			headers.write(out);
			out.write(errorMessage.getBytes());
		} catch (IOException ex) {
			return;
		}
	}

	private static Headers getDefaultHeaders(int contentLength) {
		var headers = new Headers();
		headers.put("Content-Type", "text/plain");
		headers.put("Content-Length", String.valueOf(contentLength));
		headers.put("Connection", "close");
		return headers;
	}

	public static void writeStatusLine(OutputStream out, int statusCode, String reason) throws IOException {
		var statusLine = String.format("%s %s %s\r\n", protocol, statusCode, reason);
		out.write(statusLine.getBytes());
	}

}
