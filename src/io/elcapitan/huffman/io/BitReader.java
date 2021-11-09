package io.elcapitan.huffman.io;

import java.io.*;

public class BitReader implements Closeable {
    InputStream in;
    byte buffer;
    byte currentBit;

    public BitReader(InputStream in) {
        this.in = new BufferedInputStream(in);
    }

    public BitReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public boolean readBit() throws IOException {
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

    public long readBits(int numBits) throws IOException {
        long result = 0;
        for (int i = 0; i < numBits; i++) {
            boolean bit = readBit();
            result |= (long) (bit ? 1 : 0) << i;
        }
        return result;
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    private void fillBuffer() throws IOException {
        if (available() == 0) return;
        buffer = (byte) in.read();
        currentBit = 8;
    }
}
