package io.elcapitan.huffman.io;

import java.io.*;

public class BitWriter {
    OutputStream out;
    private byte buffer;
    private int currentBit;

    public BitWriter(OutputStream out) {
        this.out = out;
        this.buffer = 0;
        this.currentBit = 0;
    }

    public BitWriter(File file) throws FileNotFoundException {
        this(new BufferedOutputStream(new FileOutputStream(file)));
    }

    public void writeBit(boolean bit) throws IOException {
        buffer = (byte) (buffer << 1 | (bit ? 1 : 0));
        currentBit++;
        if (currentBit == 8) {
            out.write(buffer);
            buffer = 0;
            currentBit = 0;
        }
    }

    public void writeBits(int bits, int numBits) throws IOException {
        for (int i = 0; i < numBits; i++) {
            writeBit((bits & (1 << i)) != 0);
        }
    }

    public void writeByte(byte nextByte) throws IOException {
        if (currentBit == 0)
            out.write(nextByte);
        else
            writeBits(nextByte, 8);
    }

    public void flush() throws IOException {
        while (currentBit > 0) writeBit(false);
        out.flush();
    }

    public void close() throws IOException {
        flush();
        out.close();
    }
}
