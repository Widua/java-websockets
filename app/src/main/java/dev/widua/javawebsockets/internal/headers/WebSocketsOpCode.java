package dev.widua.javawebsockets.internal.headers;

public enum WebSocketsOpCode {
    TEXT((byte) 0x01), BINARY((byte) 0x02);
    private final byte opcode;

    WebSocketsOpCode(byte opcode) {
        this.opcode = opcode;

    }

    public byte getOpcode(boolean fin) {
        var finByte = fin ? 0x80 : 0x00;
        return (byte) (finByte | opcode);
    }
}
