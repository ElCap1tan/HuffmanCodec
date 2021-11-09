package io.elcapitan.huffman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        HuffmanCodec huffmanCodec;
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
                    try {
                        huffmanCodec = new HuffmanCodec(str);
                        printInfo(huffmanCodec);
                        System.out.println("Saving...");
                        File file = new File("output.huff");
                        if (!Files.isWritable(Path.of(file.getAbsolutePath().replace(file.getName(), "")))) {
                            System.out.printf("File not writable to current working directory '%s'. Aborting...\n\n",
                                    file.getAbsolutePath());
                            continue;
                        }
                        huffmanCodec.saveToFile(file);
                        System.out.printf("Saved to '%s'!\n\n", file.getAbsolutePath());
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
                        huffmanCodec = new HuffmanCodec(file);
                        printInfo(huffmanCodec);
                        System.out.println("Saving to file...");
                        String filePath = file.getAbsolutePath().replace(file.getName(), "");
                        if (Files.isWritable(Path.of(filePath))) {
                            filePath += file.getName() + ".huff";
                        }
                        else {
                            System.out.printf("File not writable to '%s'. " +
                                    "Trying to save to current working directory '%s'.\n",
                                    filePath, System.getProperty("user.dir"));
                            filePath = file.getName() + ".huff";
                            if (!Files.isWritable(Path.of(filePath))) {
                                System.out.println("File not writable to current working directory. Aborting...\n\n");
                                continue;
                            }
                        }
                        huffmanCodec.saveToFile(new File(filePath));
                        System.out.printf("Saved to %s!\n\n", filePath);
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

    private static float printCodeTable(HuffmanCodec huffmanCodec) {
        float avg = 0;
        System.out.println("--- CODE TABLE ---");
        for (char symbol : huffmanCodec.getCodeDict().keySet()) {
            double freq = huffmanCodec.getFrequency(symbol);
            String code = huffmanCodec.getCode(symbol);
            avg += code.length() * freq;
            System.out.printf("[Freq: %.5f] %s => %s\n", freq, symbol == '\n' ? "CR" : symbol, code);
        }
        System.out.println("------------------");
        return avg;
    }

    private static void printInfo(HuffmanCodec huffmanCodec) {
        double avg = printCodeTable(huffmanCodec);

        String message = huffmanCodec.getMessage();
        String encoded = huffmanCodec.getEncoded();
        System.out.printf("Message [%d byte(s)]: %s%s\n" +
                        "Encoded [%d byte(s)]: %s%s\n" +
                        "Naive code length: %.3f bit/symbol\n" +
                        "Avg. code length with Huffman encoding: %.3f bit/symbol\n\n",
                message.length(), message.substring(0, Math.min(75, message.length())), message.length() > 75 ? "..." : "",
                (int) Math.ceil((double) encoded.length() / 8), encoded.substring(0, Math.min(75, encoded.length())), encoded.length() > 75 ? "..." : "",
                Math.log10(huffmanCodec.getCodeDict().size()) / Math.log10(2), avg);
    }
}
