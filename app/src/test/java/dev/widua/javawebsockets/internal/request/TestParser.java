package dev.widua.javawebsockets.internal.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestParser {

	@Test
	public void testSimpleRequest() throws IOException {
		var parser = new RequestParser();
		var req = "GET / HTTP/1.1\r\nUser-Agent: Test\r\n\r\n";

		var parsedReq = parser.parse(new ByteArrayInputStream(req.getBytes()));

		assertEquals("GET", parsedReq.getMethod());
	}

	@Test
	public void testHeaderParsing() throws IOException {
		var parser = new RequestParser();
		var req = "GET / HTTP/1.1\r\nUser-Agent: Test\r\n\r\n";

		var parsedReq = parser.parse(new ByteArrayInputStream(req.getBytes()));
		var headers = parsedReq.getHeaders();

		assertNotNull(headers);
		assertEquals("Test", headers.get("user-agent"));
		assertEquals("Test", headers.get("user-Agent"));
		assertEquals("Test", headers.get("User-Agent"));

	}

	@Test
	public void testMultipleHeadersParsing() throws IOException {
		var parser = new RequestParser();
		var req = "GET /testing HTTP/1.1\r\nUser-Agent: Test\r\nSuper-Users: Me\r\nFav-Game: Minecraft\r\n\r\n";

		var parsedReq = parser.parse(new ByteArrayInputStream(req.getBytes()));
		var headers = parsedReq.getHeaders();

		assertNotNull(headers);
		assertEquals("Test", headers.get("User-Agent"));
		assertEquals("Me", headers.get("Super-Users"));
		assertEquals("Minecraft", headers.get("Fav-Game"));
	}

	@Test
	public void testBodyParsing() throws IOException {
		var parser = new RequestParser();
		var req = "GET /greeting HTTP/1.1\r\nContent-Type: text/plain\r\nContent-Length: 11\r\n\r\nHello World";

		var parsedReq = parser.parse(new ByteArrayInputStream(req.getBytes()));
		var body = new String(parsedReq.getBody());

		assertEquals("Hello World", body);
	}

}
