package dev.widua.javawebsockets.internal.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class RequestParser {

	private ParsingState state;
	private final String CRLF = "\r\n";

	public RequestParser() {
		this.state = ParsingState.INITIALIZED;
	}

	public HttpRequest parse(InputStream input) throws IOException {
		var req = new HttpRequest();
		var buff = new byte[32];
		var readToIndex = 0;

		while (state != ParsingState.PARSING_BODY) {
			int crlfPos = findCRLF(buff, readToIndex);
			if (crlfPos == -1) {
				if (readToIndex >= buff.length) {
					buff = Arrays.copyOf(buff, buff.length * 2);
				}

				int read = input.read(buff, readToIndex, buff.length - readToIndex);
				if (read == -1)
					break;
				readToIndex += read;
				continue;
			}

			byte[] line = Arrays.copyOfRange(buff, 0, crlfPos);

			int bytesConsumed = crlfPos + CRLF.length();
			int remainingInBuff = readToIndex - bytesConsumed;
			System.arraycopy(buff, bytesConsumed, buff, 0, remainingInBuff);
			readToIndex = remainingInBuff;

			if (line.length == 0 && state == ParsingState.PARSING_HEADERS) {
				state = ParsingState.PARSING_BODY;
				break;
			}
			parseLine(line, req);
		}

		var cl = req.getHeaders().get("Content-Length");
		if (cl != "") {
			int length = Integer.parseInt(cl.trim());
			byte[] body = new byte[length];

			int bytesFromBuff = Math.min(readToIndex, length);
			System.arraycopy(buff, 0, body, 0, bytesFromBuff);

			if (bytesFromBuff < length) {
				input.readNBytes(body, bytesFromBuff, length - bytesFromBuff);
			}
			req.setBody(body);
			state = ParsingState.DONE;
		}
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

	private int findCRLF(byte[] data, int readToIndex) {
		for (int i = 0; i < readToIndex - 1; i++) {
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
