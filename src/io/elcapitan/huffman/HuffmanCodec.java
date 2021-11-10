package io.elcapitan.huffman;

import io.elcapitan.huffman.io.BitReader;
import io.elcapitan.huffman.io.BitWriter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class HuffmanCodec {
    private String message;
    private String code;
    private HuffmanNode root;
    private Map<Character, String> codeDict;
    private Map<Character, Double> frequencies;

    public HuffmanCodec() {
        this("");
    }

    public HuffmanCodec(String message) throws NullPointerException {
        encode(message);
    }

    public HuffmanCodec(InputStream in) throws NullPointerException {
        encode(in);
    }

    public HuffmanCodec(File file) throws IOException {
        encode(file);
    }

    public void encode(String message) throws NullPointerException {
        if (message == null) throw new NullPointerException("Message cannot be null");

        this.message = message;
        generate();
    }

    public void encode(InputStream in) throws NullPointerException {
        if (in == null) throw new NullPointerException("InputStream cannot be null");

        encode(new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n")));
    }

    public void encode(File file) throws IOException {
        if (file == null) throw new NullPointerException("File cannot be null");

        encode(new FileInputStream(file));
    }

    public void saveToFile(File file) throws IOException {
        if (file == null) throw new NullPointerException("File cannot be null");

        BitWriter writer = new BitWriter(file);
        int bitsWritten = writeTree(writer);
        writeDiscardBits(bitsWritten, writer);
        writeMessage(writer);
        writer.close();
    }

    public void decode(File file) throws IOException {
        if (file == null) throw new NullPointerException("File cannot be null");

        decode(new FileInputStream(file));
    }

    public void decode(InputStream in) throws IOException {
        if (in == null) throw new NullPointerException("InputStream cannot be null");

        BitReader reader = new BitReader(in);
        decodeTree(reader);
        generateCodeDict();
        readCode(reader);
        decodeMessage();
        generateFrequencies();
        reader.close();
    }

    public void decode(String code, Map<Character, String> dictionary) {
        if (code == null) throw new NullPointerException("Code cannot be null");
        if (dictionary == null) throw new NullPointerException("Dictionary cannot be null");

        this.code = code;
        codeDict = dictionary;
        decodeMessage();
        generateFrequencies();
    }

    private void decodeTree(BitReader reader) throws IOException {
        frequencies = new HashMap<>();
        root = decodeNode(reader);
    }

    private HuffmanNode decodeNode(BitReader reader) throws IOException {
        if (reader.readBit()) {
            return new HuffmanNode(0f, reader.readChar());
        } else {
            return new HuffmanNode(decodeNode(reader), decodeNode(reader));
        }
    }

    private void decodeMessage() {
        StringBuilder messageBuilder = new StringBuilder();
        for (int start = 0; start < code.length();) {
            for (int end = start + 1; end <= code.length(); end++) {
                String subCode = code.substring(start, end);
                if (codeDict.containsValue(subCode)) {
                    messageBuilder.append(codeDict.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(subCode))
                            .findFirst()
                            .get()
                            .getKey());
                    start = end;
                } else if (end == code.length()) {
                    start++;
                }
            }
        }
        message = messageBuilder.toString();
    }

    private void readCode(BitReader reader) throws IOException {
        byte discardBits = (byte) reader.readBits(3);

        StringBuilder codeBuilder = new StringBuilder();
        while (reader.hasNext()) {
            codeBuilder.append(reader.readBit() ? "1" : "0");
        }
        code = codeBuilder.substring(0, codeBuilder.length() - discardBits);
    }

    public double getFrequency(char c) {
        return frequencies.getOrDefault(c, 0d);
    }

    public String getCode(char c) {
        return codeDict.get(c);
    }

    public Map<Character, String> getCodeDict() {
        return codeDict;
    }

    public String getMessage() {
        return message;
    }

    public String getEncoded() {
        return code;
    }

    private int writeTree(BitWriter writer) throws IOException {
        return writeNode(root, writer);
    }

    private int writeNode(HuffmanNode n, BitWriter writer) throws IOException {
        assert n != null && writer != null;

        if (n.isLeaf()) {
            writer.writeBit(true);
            writer.writeByte((byte) n.getChar());
            return 9;
        } else {
            int bits = 1;
            writer.writeBit(false);
            bits += writeNode(n.getLeft(), writer);
            bits += writeNode(n.getRight(), writer);
            return bits;
        }
    }

    private void writeDiscardBits(int bitsAlreadyWritten, BitWriter writer) throws IOException {
        assert writer != null;
        writer.writeBits(8 - ((bitsAlreadyWritten + code.length() + 3) % 8), 3);
    }

    private void writeMessage(BitWriter writer) throws IOException {
        assert writer != null;

        for (char b : code.toCharArray()) {
            writer.writeBit(b == '1');
        }
    }

    private void generate() {
        generateFrequencies();
        generateTree();
        generateCodeDict();
        encode();
    }

    private void generateFrequencies() {
        frequencies = new HashMap<>();
        char[] unique = message.chars().distinct()
                .mapToObj(c -> String.valueOf((char) c)).collect(Collectors.joining()).toCharArray();

        for (char letter : unique) {
            double f = calculateFrequency(letter);
            frequencies.put(letter, f);
        }
    }

    private double calculateFrequency(char letter) {
        int count = 0;
        for (char c : message.toCharArray()) {
            if (letter == c) count++;
        }
        return (double) count / message.length();
    }

    private void generateTree() throws NullPointerException{
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (char letter : frequencies.keySet()) {
            pq.add(new HuffmanNode(getFrequency(letter), letter));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();

            assert left != null && right != null;

            pq.add(new HuffmanNode(left, right));
        }
        root = pq.poll();
    }

    private void generateCodeDict() {
        codeDict = new HashMap<>();
        if (root != null) {
            if (root.isLeaf()) codeDict.put(root.getChar(), "0");
            else generateCodes(root, "");
        }
    }

    private void generateCodes(HuffmanNode n, String codePart) {
        assert n != null && codePart != null;

        if (n.isLeaf()) {
            codeDict.put(n.getChar(), codePart);
            return;
        }
        generateCodes(n.getLeft(), codePart + "0");
        generateCodes(n.getRight(), codePart + "1");
    }

    private void encode() {
        StringBuilder builder = new StringBuilder();
        for (char c : message.toCharArray()) {
            builder.append(codeDict.get(c));
        }
        code = builder.toString();
    }
}
