package com.alint.disertatie.engine.javadssengine.util;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Log4j2
public class STIJavaEngineTransceiver {
    private final DataInputStream in;
    private final DataOutputStream out;

    public STIJavaEngineTransceiver(InputStream in, OutputStream out) {
        this.in = new DataInputStream(in);
        this.out = new DataOutputStream(out);
    }

    private void send(byte[] cbuf) throws IOException {
        out.write(cbuf);
        out.flush();
    }

    private void read(byte[] cbuf) throws IOException {
        int bytesRead = 0;
        int leftToRead = cbuf.length;
        while (leftToRead > 0) {
//                throw new IOException("Reading error: expected " + cbuf.length + " bytes but read " + bytesRead);
            int currBytesRead = in.read(cbuf,bytesRead, leftToRead);
            bytesRead += currBytesRead;
            leftToRead -= currBytesRead;
        }
    }

    public void sendUInt32(int toSend) throws IOException {
        if (Integer.compareUnsigned(toSend, Integer.MAX_VALUE) > 0) {
            throw new RuntimeException("Could not send an integer bigger than 4 bytes");
        }
        byte[] cbuf = new byte[4];
        cbuf[0] = (byte) (toSend & 0x000000FF);
        cbuf[1] = (byte) ((toSend & 0x0000FF00) >> 8);
        cbuf[2] = (byte) ((toSend & 0x00FF0000) >> 16);
        cbuf[3] = (byte) ((toSend & 0xFF000000) >> 24);

        send(cbuf);
    }

    public int readUInt32() throws IOException {
        byte[] cbuf = new byte[4];
        read(cbuf);

        return ByteBuffer.wrap(cbuf).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public void sendMessage(String message) throws IOException {
        sendUInt32(message.getBytes(StandardCharsets.UTF_8).length);
        send(message.getBytes(StandardCharsets.UTF_8));
    }

    public void sendBytes(byte[] bytes) throws IOException {
        sendUInt32(bytes.length);
        send(bytes);
    }

    public byte[] readBytes() throws IOException {
        int messageLen = readUInt32();
        byte[] cbuf = new byte[messageLen];
        read(cbuf);

        return cbuf;
    }

    public String readMessage() throws IOException {
        int messageLen = readUInt32();
        byte[] cbuf = new byte[messageLen];
        read(cbuf);

        return new String(cbuf,StandardCharsets.UTF_8);
    }

    public void closeConnections() throws IOException {
        in.close();
        out.close();
    }
}
