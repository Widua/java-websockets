package dev.widua.javawebsockets.internal.request;

import dev.widua.javawebsockets.internal.headers.Headers;

public class HttpRequest {
	private StatusLine statusLine;
	private Headers headers;
	private byte[] body;

	public HttpRequest() {
		this.headers = new Headers();
		this.body = new byte[0];
	}

	public String getMethod() {
		return statusLine.method();
	}

	public String getTarget() {
		return statusLine.target();
	}

	public String getVersion() {
		return statusLine.Version();
	}

	public void setStatusLine(String method, String target, String version) {
		this.statusLine = new StatusLine(method, target, version);
	}

	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	private record StatusLine(String method, String target, String Version) {
		@Override
		public final String toString() {
			return String.format("%s %s %s", method, target, Version);
		}
	}

	@Override
	public String toString() {
		return String.format("%s\n%s\n%s", statusLine.toString(), headers.toString(), new String(body));
	}

}
