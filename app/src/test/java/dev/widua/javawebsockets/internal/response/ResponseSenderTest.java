package dev.widua.javawebsockets.internal.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseSenderTest {


    @Test
    public void testCreationOfWebsocketResponseTest(){
        var clientKey = "dGhlIHNhbXBsZSBub25jZQ==";

        var response = ResponseSender.createWebsocketResponseKey(clientKey);

        assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=",response);
    }

}