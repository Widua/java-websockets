package dev.widua.javawebsockets.internal.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

class RequestParser {

	private ParsingState state;
	private final String CRLF = "\r\n";

	public RequestParser() {
		this.state = ParsingState.INITIALIZED;
	}

	public HttpRequest parse(InputStream input) throws IOException {
		var rawData = input.readAllBytes();
		var req = new HttpRequest();

		while (state != ParsingState.PARSING_BODY) {

			var crlfPos = findCRLF(rawData);
			if (crlfPos == -1 || crlfPos == 0) {
				state = ParsingState.PARSING_BODY;
				rawData = Arrays.copyOfRange(rawData, CRLF.length(), rawData.length);
				break;
			}
			parseLine(Arrays.copyOfRange(rawData, 0, crlfPos), req);
			rawData = Arrays.copyOfRange(rawData, crlfPos + CRLF.length(), rawData.length);
		}
		parseLine(rawData, req);
		return req;
	}

	private void parseLine(byte[] data, HttpRequest req) {
		switch (state) {
			case INITIALIZED -> {
				var stringified = new String(data);
				var statusLine = stringified.split(" ");
				req.setStatusLine(statusLine[0], statusLine[1], statusLine[2]);
				state = ParsingState.PARSING_HEADERS;
				return;
			}
			case PARSING_HEADERS -> {
				var stringified = new String(data);
				var header = stringified.split(":");
				req.getHeaders().put(header[0].trim(), header[1].trim());

			}
			case DONE -> {
				return;
			}
			case PARSING_BODY -> {
				req.setBody(data);
			}
		}
	}

	private int findCRLF(byte[] data) {
		for (int i = 0; i < data.length - 1; i++) {
			if (data[i] == '\r' && data[i + 1] == '\n') {
				return i;
			}
		}
		return -1;
	}

	enum ParsingState {
		INITIALIZED, PARSING_HEADERS, PARSING_BODY, DONE
	}

}
