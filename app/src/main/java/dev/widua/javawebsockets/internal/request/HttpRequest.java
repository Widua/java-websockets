package dev.widua.javawebsockets.internal.request;

import dev.widua.javawebsockets.internal.headers.Headers;

public class HttpRequest {
	private StatusLine statusLine;
	private Headers headers;
	private Byte[] body;

	public StatusLine getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(StatusLine statusLine) {
		this.statusLine = statusLine;
	}

	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	public Byte[] getBody() {
		return body;
	}

	public void setBody(Byte[] body) {
		this.body = body;
	}

	private record StatusLine(String method, String target, String Version) {
	}
}
