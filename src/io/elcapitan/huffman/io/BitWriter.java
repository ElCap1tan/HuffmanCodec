package io.elcapitan.huffman.io;

import java.io.*;

public class BitWriter implements Closeable, Flushable {
    OutputStream out;
    private byte buffer;
    private int currentBit;

    public BitWriter(OutputStream out) throws NullPointerException {
        if (out == null) throw new NullPointerException("OutputStream cannot be null");

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

    public void writeBits(long bits, int numBits) throws IOException {
        for (int i = 0; i < numBits; i++) {
            writeBit((bits >> i & 1) != 0);
        }
    }

    public void writeByte(byte nextByte) throws IOException {
        if (currentBit == 0)
            out.write(nextByte);
        else
            writeBits(nextByte, 8);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        for (byte b : bytes)
            writeByte(b);
    }

    public void writeShort(short nextShort) throws IOException {
        writeBits(nextShort, 16);
    }

    public void writeInt(int nextInt) throws IOException {
        writeBits(nextInt, 32);
    }

    public void writeLong(long nextLong) throws IOException {
        writeBits(nextLong, 64);
    }

    public void writeFloat(float nextFloat) throws IOException {
        writeInt(Float.floatToIntBits(nextFloat));
    }

    public void writeDouble(double nextDouble) throws IOException {
        writeLong(Double.doubleToLongBits(nextDouble));
    }

    public void writeString(String nextString) throws IOException {
        if (nextString == null) throw new NullPointerException("String cannot be null");

        for (char c : nextString.toCharArray())
            writeByte((byte) c);
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
