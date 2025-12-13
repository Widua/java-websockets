package dev.widua.javawebsockets.internal.headers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Headers {
	private static final String CRLF = "\r\n";
	private Map<String, String> headers;

	public Headers() {
		this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}

	public void put(String key, String value) {
		Objects.requireNonNull(key, "Key shouldn't be null");
		Objects.requireNonNullElse(key, "Value shouldn't be null");

		headers.put(key, value);
	}

	public String get(String key) {
		Objects.requireNonNull(key, "Key shouldn't be null");

		return headers.get(key);
	}

	public void write(BufferedWriter writer) throws IOException {

		for (var entrySet : headers.entrySet()) {
			var headerLine = String.format("%s: %s%s", entrySet.getKey(), entrySet.getValue(), CRLF);
			writer.write(headerLine);
		}

		writer.write(CRLF);
		writer.flush();
	}
}
