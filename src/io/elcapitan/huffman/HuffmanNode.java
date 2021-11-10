package io.elcapitan.huffman;

public class HuffmanNode implements Comparable<HuffmanNode> {
    private final double freq;
    private HuffmanNode left, right;
    private char c;

    public HuffmanNode(double freq, char c) {
        this.freq = freq;
        this.c = c;
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.freq = left.freq + right.freq;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(HuffmanNode that) {
        if (this.freq == that.freq) return 0;
        return this.freq < that.freq ? -1 : 1;
    }

    public double getFreq() {
        return freq;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    public char getChar() {
        return c;
    }
}
