import java.io.*;
import java.net.*;

public class StatTDMClient {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java StatTDMClient <input1.txt> <input2.txt> ...");
            return;
        }

        Socket socket = new Socket("192.168.50.159", 5000);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        int N = args.length;
        FileInputStream[] inputs = new FileInputStream[N];
        for (int i = 0; i < N; i++) {
            inputs[i] = new FileInputStream(args[i]);
        }

        boolean allEOF = false;
        int frameCounter = 1;

        while (!allEOF) {
            allEOF = true;
            ByteArrayOutputStream frame = new ByteArrayOutputStream();

            for (int i = 0; i < N; i++) {
                int val = inputs[i].read();
                if (val != -1) {
                    frame.write(i + 1);
                    frame.write(val);
                    allEOF = false;
                }
            }

            byte[] frameBytes = frame.toByteArray();
            if (frameBytes.length > 0) {
                System.out.print("Sending Frame #" + frameCounter + ": ");
                for (int i = 0; i < frameBytes.length; i += 2)
                    System.out.print("[" + frameBytes[i] + ", " + (char) frameBytes[i + 1] + "] ");
                System.out.println();

                out.writeInt(frameBytes.length);
                out.write(frameBytes);
                frameCounter++;
            }
        }

        for (FileInputStream input : inputs)
            input.close();
        socket.close();
    }
}