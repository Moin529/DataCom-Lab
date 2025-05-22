import java.io.*;
import java.net.*;

public class Multiplexer {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("192.168.50.159", 5000);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        String[] inputFiles = {"input1.txt", "input2.txt", "input3.txt"};
        int T = 30, N = 3;
        FileInputStream[] inputs = new FileInputStream[N];

        for (int i = 0; i < N; i++)
            inputs[i] = new FileInputStream(inputFiles[i]);

        boolean allEOF = false;

        while (!allEOF) {
            allEOF = true;
            byte[] packet = new byte[N * T];

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < T; j++) {
                    int val = inputs[i].read();
                    if (val == -1)
                        packet[i * T + j] = (byte) '#';
                    else {
                        packet[i * T + j] = (byte) val;
                        allEOF = false;
                    }
                }
            }

            if (!allEOF) {
                System.out.print("Sending Packet: ");
                for (byte b : packet)
                    System.out.print((char) b + "");
                System.out.println();
                out.write(packet);
            }
        }

        for (int i = 0; i < N; i++)
            inputs[i].close();
        socket.close();
    }
}

