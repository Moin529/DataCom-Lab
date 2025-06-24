import java.io.*;
import java.net.*;

public class Server {

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
            if (remainder.charAt(0) == '1') {
                remainder = xor(remainder, generator);
            } else {
                remainder = xor(remainder, "0".repeat(k));
            }

            if (i < data.length()) {
                remainder = remainder.substring(1) + data.charAt(i);
            } else {
                remainder = remainder.substring(1);
            }
        }
        return remainder;
    }

    private static String flipRandomBit(String data, double flipChance) {
        double randomValue = Math.random();  

        if (randomValue < flipChance) {
            System.out.println("No bit flipped (flip chance not met)");
            return data; 
        }

        int index = (int) (Math.random() * data.length());
        char flipped = (data.charAt(index) == '0') ? '1' : '0';
        String flippedData = data.substring(0, index) + flipped + data.substring(index + 1);
        System.out.println("Bit flipped at position: " + index + " (flip chance = " + flipChance + ")");
        return flippedData;
    }

    public static void main(String[] args) {
        int port = 5000;
        double flipChance = 0.3;  

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is running and waiting on port " + port + "...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected from: " + socket.getInetAddress());

            DataInputStream in = new DataInputStream(socket.getInputStream());
            String received = in.readUTF();
            String[] parts = received.split(":");
            String codeword = parts[0];
            String generator = parts[1];

            System.out.println("Original Received Codeword: " + codeword);
            System.out.println("Using Generator Polynomial: " + generator);

            codeword = flipRandomBit(codeword, flipChance);
            System.out.println("Final Codeword for CRC Check: " + codeword);

            String remainder = crcRemainder(codeword, generator);
            System.out.println("Calculated Remainder: " + remainder);

            if (remainder.contains("1")) {
                System.out.println("Error detected in transmission!");
            } else {
                System.out.println("No error detected in transmission.");
            }

            in.close();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }
}
