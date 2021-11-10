package io.elcapitan.huffman.io;

import java.io.*;

public class BitReader implements Closeable {
    private final InputStream in;
    private byte buffer;
    private byte currentBit;
    private boolean closed;
    private boolean eof;

    public BitReader(InputStream in) {
        this.in = new BufferedInputStream(in);
        closed = false;
        eof = false;
    }

    public BitReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public boolean readBit() throws IOException {
        if (closed) {
            throw new IOException("BitReader is closed.");
        }
        if (currentBit == 0) {
            fillBuffer();
        }
        return ((buffer >> --currentBit) & 1) == 1;
    }

    public byte readByte() throws IOException {
        return (byte) readBits(8);
    }

    public char readChar() throws IOException {
        return (char) readByte();
    }

    public int readInt() throws IOException {
        return (int) readBits(32);
    }

    public long readLong() throws IOException {
        return readBits(64);
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public String readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        char c;
        while ((c = readChar()) != '\0') {
            sb.append(c);
        }
        return sb.toString();
    }

    public long readBits(int numBits) throws IOException {
        long result = 0;
        for (int i = 0; i < numBits; i++) {
            boolean bit = readBit();
            result |= (long) (bit ? 1 : 0) << i;
        }
        return result;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean hasNext() throws IOException {
        if (closed) {
            throw new IOException("BitReader is closed.");
        }
        if (currentBit == 0) {
            fillBuffer();
        }
        return !eof;
    }

    public void close() throws IOException {
        in.close();
        closed = true;
    }

    private void fillBuffer() throws IOException {
        if (eof) {
            throw new EOFException();
        }
        buffer = (byte) in.read();
        if (buffer == -1) {
            eof = true;
        }
        currentBit = 8;
    }
}
