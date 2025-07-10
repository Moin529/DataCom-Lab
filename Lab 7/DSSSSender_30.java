import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DSSSSender_30 {
    public static int[] spreadBit(int bit, int[] chipCode) {
        int[] spread = new int[chipCode.length];
        for (int i = 0; i < chipCode.length; i++) {
            spread[i] = bit ^ chipCode[i];
        }
        return spread;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {

            System.out.print("Enter chip code : ");
            String chipStr = sc.nextLine().trim();
            int[] chipCode = chipStr.chars().map(c -> c - '0').toArray();

            File dir = new File(".");
            File[] inputFiles = dir.listFiles((d, name) -> name.startsWith("input") && name.endsWith(".txt"));
            if (inputFiles == null || inputFiles.length == 0) {
                System.out.println("No input files found.");
                return;
            }
            Socket socket = new Socket("localhost", 5000); // Receiver's IP address here
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(chipCode.length);
            for (int bit : chipCode) {
                dos.writeInt(bit);
            }

            dos.writeInt(inputFiles.length);

            for (File file : inputFiles) {
                String filename = file.getName();
                System.out.println("Processing " + filename);

                String content = Files.readString(file.toPath());
                List<Integer> spreadBits = new ArrayList<>();

                for (char ch : content.toCharArray()) {
                    String binary = String.format("%8s", Integer.toBinaryString(ch)).replace(' ', '0');
                    for (char b : binary.toCharArray()) {
                        int bit = b - '0';
                        int[] spread = spreadBit(bit, chipCode);
                        for (int s : spread) {
                            spreadBits.add(s);
                        }
                    }
                }

                dos.writeInt(filename.length());
                dos.writeBytes(filename);

                dos.writeInt(spreadBits.size());

                for (int bit : spreadBits) {
                    dos.writeInt(bit);
                }

                System.out.println("Sent data for " + filename);
            }

            dos.close();
            socket.close();
            System.out.println("Sender done.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}