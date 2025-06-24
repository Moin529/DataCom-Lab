import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static String toBinary(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return result.toString();
    }

    private static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < b.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    private static String crcRemainder(String data, String generator) {
        int k = generator.length();
        String remainder = data.substring(0, k);

        for (int i = k; i < data.length(); i++) {
            if (remainder.charAt(0) == '1') {
                remainder = xor(remainder, generator) + data.charAt(i);
            } else {
                remainder = xor(remainder, "0".repeat(k)) + data.charAt(i);
            }
            remainder = remainder.substring(1);
        }

        if (remainder.charAt(0) == '1') {
            remainder = xor(remainder, generator);
        } else {
            remainder = xor(remainder, "0".repeat(k));
        }

        return remainder.substring(1);
    }

    public static void main(String[] args) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("input.txt"));
            String message = fileReader.readLine();
            fileReader.close();

            System.out.println("File Content: " + message);
            String binaryData = toBinary(message);
            System.out.println("Converted Binary Data: " + binaryData);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Generator Polynomial (e.g., 10011): ");
            String generator = scanner.nextLine();
            scanner.close();

            int k = generator.length();
            String dataToDivide = binaryData + "0".repeat(k - 1);
            System.out.println("After Appending zeros Data to Divide: " + dataToDivide);

            String crc = crcRemainder(dataToDivide, generator);
            System.out.println("CRC Remainder: " + crc);

            String codeword = binaryData + crc;
            System.out.println("Transmitted Codeword to Server: " + codeword);

            Socket socket = new Socket("192.168.185.188", 5000); 
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(codeword + ":" + generator); 
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.net.*;
import java.util.*;

public class CRCClient {

    private static String toBinary(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return result.toString();
    }

    private static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < b.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    private static String crcRemainder(String data, String generator) {
        int k = generator.length();
        String remainder = data.substring(0, k);

        for (int i = k; i < data.length(); i++) {
            remainder = xor(remainder, (remainder.charAt(0) == '1') ? generator : "0".repeat(k)) + data.charAt(i);
            remainder = remainder.substring(1);
        }

        remainder = xor(remainder, (remainder.charAt(0) == '1') ? generator : "0".repeat(k));
        return remainder.substring(1);
    }

    public static void main(String[] args) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("input.txt"));
            String message = fileReader.readLine();
            fileReader.close();

            System.out.println("File Content: " + message);
            String binaryData = toBinary(message);
            System.out.println("Converted Binary Data: " + binaryData);

            Map<Integer, String> crcMap = new HashMap<>();
            crcMap.put(1, "100000111");       // CRC-8
            crcMap.put(2, "11000110011");     // CRC-10
            crcMap.put(3, "11000000000000101"); // CRC-16
            crcMap.put(4, "100000100110000010001110110110111"); // CRC-32

            System.out.println("Select CRC Polynomial:\n1. CRC-8\n2. CRC-10\n3. CRC-16\n4. CRC-32");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            String generator = crcMap.getOrDefault(choice, crcMap.get(1));
            scanner.close();

            int k = generator.length();
            String dataToDivide = binaryData + "0".repeat(k - 1);
            System.out.println("After Appending zeros Data to Divide: " + dataToDivide);

            String crc = crcRemainder(dataToDivide, generator);
            System.out.println("CRC Remainder: " + crc);

            String codeword = binaryData + crc;
            System.out.println("Transmitted Codeword to Server: " + codeword);

            Socket socket = new Socket("localhost", 5000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(codeword + ":" + generator);
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
