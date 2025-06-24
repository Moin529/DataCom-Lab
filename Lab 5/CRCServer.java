import java.io.*;
import java.net.*;
import java.util.*;

public class CRCServer {

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

        for (int i = k; i <= data.length(); i++) {
            remainder = xor(remainder, (remainder.charAt(0) == '1') ? generator : "0".repeat(k));
            remainder = (i < data.length()) ? remainder.substring(1) + data.charAt(i) : remainder.substring(1);
        }

        return remainder;
    }

    private static String flipBit(String data, int index) {
        char[] chars = data.toCharArray();
        chars[index] = (chars[index] == '0') ? '1' : '0';
        return new String(chars);
    }

    private static String flipRandomBit(String data, double flipChance) {
        if (Math.random() < flipChance) {
            int index = (int) (Math.random() * data.length());
            data = flipBit(data, index);
            System.out.println("Bit flipped at position: " + index);
        } else {
            System.out.println("No bit flipped (flip chance not met)");
        }
        return data;
    }

    public static void main(String[] args) throws IOException {
        int port = 5000;
        double flipChance = 0.5;

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server waiting at port " + port + "...");

        Socket socket = serverSocket.accept();
        DataInputStream in = new DataInputStream(socket.getInputStream());

        String[] parts = in.readUTF().split(":");
        String codeword = parts[0];
        String generator = parts[1];

        System.out.println("Original Received Codeword: " + codeword);
        System.out.println("Generator Polynomial: " + generator);

        codeword = flipRandomBit(codeword, flipChance);
        System.out.println("Final Codeword for CRC Check: " + codeword);

        String remainder = crcRemainder(codeword, generator);
        System.out.println("Calculated Remainder: " + remainder);

        if (remainder.contains("1")) {
            System.out.println("Error detected in transmission. Attempting correction...");

            List<Integer> fixableIndices = new ArrayList<>();
            for (int i = 0; i < codeword.length(); i++) {
                String test = flipBit(codeword, i);
                if (crcRemainder(test, generator).replace("0", "").isEmpty()) {
                    fixableIndices.add(i);
                }
            }

            if (fixableIndices.size() == 1) {
                int fixIndex = fixableIndices.get(0);
                String corrected = flipBit(codeword, fixIndex);
                System.out.println("Single-bit error corrected at position " + fixIndex);
                System.out.println("Corrected Codeword: " + corrected);
            } else if (fixableIndices.isEmpty()) {
                System.out.println("Uncorrectable: No valid single-bit correction found.");
            } else {
                System.out.println("Uncorrectable: Multiple possible corrections found.");
            }

        } else {
            System.out.println("No error detected in transmission.");
        }

        in.close();
        socket.close();
        serverSocket.close();
    }
}