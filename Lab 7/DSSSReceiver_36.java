import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DSSSReceiver_36 {

    public static int despreadBit(int[] chunk, int[] chipCode) {
        int countOnes = 0;
        for (int i = 0; i < chipCode.length; i++) {
            int val = chunk[i] ^ chipCode[i];
            if (val == 1) countOnes++;
        }
        return countOnes > (chipCode.length / 2) ? 1 : 0;
    }

    public static void main(String[] args) {
        try {
            int port = 5000;  
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Receiver listening on port " + port);

            Socket clientSocket = serverSocket.accept();
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

            int chipLen = dis.readInt();
            int[] chipCode = new int[chipLen];
            for (int i = 0; i < chipLen; i++) {
                chipCode[i] = dis.readInt();
            }
            System.out.print("Received chip code: ");
            for (int b : chipCode) System.out.print(b);
            System.out.println();

            int fileCount = dis.readInt();

            for (int f = 0; f < fileCount; f++) {
                int filenameLen = dis.readInt();
                byte[] filenameBytes = new byte[filenameLen];
                dis.readFully(filenameBytes);
                String filename = new String(filenameBytes);

                System.out.println("Receiving file: " + filename);

                int spreadSize = dis.readInt();
                int[] spreadBits = new int[spreadSize];
                for (int i = 0; i < spreadSize; i++) {
                    spreadBits[i] = dis.readInt();
                }

                String spreadFilename = "spread_" + filename;
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(spreadFilename))) {
                    for (int bit : spreadBits) {
                        bw.write(bit + " ");
                    }
                }

                List<Integer> recoveredBits = new ArrayList<>();
                for (int i = 0; i < spreadSize; i += chipLen) {
                    int[] chunk = new int[chipLen];
                    System.arraycopy(spreadBits, i, chunk, 0, chipLen);
                    int bit = despreadBit(chunk, chipCode);
                    recoveredBits.add(bit);
                }
                
                StringBuilder recoveredText = new StringBuilder();

                for (int i = 0; i < recoveredBits.size(); i += 8) {
                    StringBuilder byteStr = new StringBuilder();

                    for (int j = 0; j < 8; j++) {
                        byteStr.append(recoveredBits.get(i + j));
                    }
                    int charCode = Integer.parseInt(byteStr.toString(), 2);
                    recoveredText.append((char) charCode);
                }


                String recoveredFilename = "recovered_" + filename;
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(recoveredFilename))) {
                    bw.write(recoveredText.toString());
                }

                System.out.println("Recovered file saved as: " + recoveredFilename);
            }

            dis.close();
            clientSocket.close();
            serverSocket.close();
            System.out.println("Receiver done.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}