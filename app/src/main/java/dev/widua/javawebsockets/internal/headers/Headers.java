package dev.widua.javawebsockets.internal.headers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Headers {
	private static final String CRLF = "\r\n";
	private Map<String, String> headers;

	public Headers() {
		this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}

	public void put(String key, String value) {
		Objects.requireNonNull(key, "Key shouldn't be null");
		Objects.requireNonNull(value, "Value shouldn't be null");

		headers.put(key, value);
	}

	public String get(String key) {
		Objects.requireNonNull(key, "Key shouldn't be null");

		return headers.getOrDefault(key, "");
	}

	public void write(OutputStream out) throws IOException {

		for (var entrySet : headers.entrySet()) {
			var headerLine = String.format("%s: %s%s", entrySet.getKey(), entrySet.getValue(), CRLF);
			out.write(headerLine.getBytes());
		}

		out.write(CRLF.getBytes());
		out.flush();
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		for (Entry<String, String> entrySet : headers.entrySet()) {
			sb.append(String.format("%s:%s\n", entrySet.getKey(), entrySet.getValue()));

		}
		return sb.toString();
	}
}
