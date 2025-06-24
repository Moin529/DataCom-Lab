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
            Scanner scanner = new Scanner(System.in);

            BufferedReader fileReader = new BufferedReader(new FileReader("input.txt"));
            String message = fileReader.readLine();
            fileReader.close();

            System.out.println("File Content: " + message);
            String binaryData = toBinary(message);
            System.out.println("Converted Binary Data: " + binaryData);

            System.out.println("Select CRC Polynomial Type:\n1. CRC-8\n2. CRC-10\n3. CRC-16\n4. CRC-32");
            int choice = scanner.nextInt();
            scanner.nextLine();

            int expectedLength;
            switch (choice) {
                case 1 -> expectedLength = 9;
                case 2 -> expectedLength = 11;
                case 3 -> expectedLength = 17;
                case 4 -> expectedLength = 33;
                default -> {
                    System.out.println("Invalid choice.");
                    scanner.close();
                    return;
                }
            }


            System.out.println("Enter a CRC generator polynomial of length " + expectedLength + " bits:");
            String generator = scanner.nextLine().trim();

            if (!generator.matches("[01]{" + expectedLength + "}")) {
                System.out.println("Invalid generator polynomial. Must be a binary string of length " + expectedLength + ".");
                return;
            }

            scanner.close();

            System.out.println("Binary Input: " + binaryData);

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
