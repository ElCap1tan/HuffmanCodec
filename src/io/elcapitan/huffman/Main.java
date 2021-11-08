package io.elcapitan.huffman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        HuffmanCoder coder;
        Scanner input = new Scanner(System.in);

        int opCode;
        do {
            opCode = getOperation(input);
            switch (opCode) {
                case 0 -> System.out.println("Exiting...");
                case 1 -> {
                    System.out.print("Enter the string to encode: ");
                    String str = input.nextLine();
                    System.out.println("Encoding...");
                    coder = new HuffmanCoder(str);
                    printInfo(coder);
                    try {
                        System.out.println("Saving...");
                        coder.saveToFile(new File("output.huff"));
                        System.out.println("Saved!\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case 2 -> {
                    System.out.print("Enter path to file: ");
                    String path = input.nextLine();
                    File file = new File(path);
                    try {
                        System.out.println("Encoding...");
                        coder = new HuffmanCoder(file);
                        printInfo(coder);
                        System.out.println("Saving to file...");
                        coder.saveToFile(new File("output.huff"));
                        System.out.println("Saved!\n");
                    } catch (FileNotFoundException e) {
                        System.out.println("File not found!\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                default -> System.out.println("Invalid input! Try again.\n");
            }
        } while (opCode != 0);
    }

    private static int getOperation(Scanner input) {
        String tmp;
        int opCode;

        while (true) {
            printMenu();
            System.out.print("\nEnter your choice: ");
            tmp = input.nextLine();
            try {
                opCode = Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                System.out.println("Your input is not a number. Try again.\n");
                continue;
            }
            return opCode;
        }
    }

    private static void printMenu() {
        System.out.println("----- Menu -----\n" +
                "What would you like to do?\n" +
                "[0] Exit\n" +
                "[1] Encode String\n" +
                "[2] Encode File");
    }

    private static float printCodeTable(HuffmanCoder coder) {
        float avg = 0;
        System.out.println("--- CODE TABLE ---");
        for (char symbol : coder.getCodeDict().keySet()) {
            double freq = coder.getFrequency(symbol);
            String code = coder.getCode(symbol);
            avg += code.length() * freq;
            System.out.printf("[Freq: %.5f] %s => %s\n", freq, symbol == '\n' ? "CR" : symbol, code);
        }
        System.out.println("------------------");
        return avg;
    }

    private static void printInfo(HuffmanCoder coder) {
        double avg = printCodeTable(coder);

        String message = coder.getMessage();
        String encoded = coder.getEncoded();
        System.out.printf("Message [%d byte(s)]: %s%s\n" +
                        "Encoded [%d byte(s)]: %s%s\n" +
                        "Naive code length: %.3f bit/symbol\n" +
                        "Avg. code length with Huffman encoding: %.3f bit/symbol\n\n",
                message.length(), message.substring(0, Math.min(75, message.length())), message.length() > 75 ? "..." : "",
                (int) Math.ceil((double) encoded.length() / 8), encoded.substring(0, Math.min(75, encoded.length())), encoded.length() > 75 ? "..." : "",
                Math.log10(coder.getCodeDict().size()) / Math.log10(2), avg);
    }
}
