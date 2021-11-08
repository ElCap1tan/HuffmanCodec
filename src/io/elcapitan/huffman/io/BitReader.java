package io.elcapitan.huffman.io;

import java.io.*;

public class BitReader {
    InputStream in;
    byte buffer;

    public BitReader(InputStream in) {
        this.in = new BufferedInputStream(in);
        buffer = 0;
    }

    public BitReader(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }
}
