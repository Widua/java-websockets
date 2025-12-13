package dev.widua.javawebsockets.internal.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestHeaders {

	@Test
	public void testCaseInsensitivity() {
		var headers = new Headers();

		headers.put("Content-Type", "application/json");

		var contentType = headers.get("content-type");

		assertEquals("application/json", contentType);
	}

}
