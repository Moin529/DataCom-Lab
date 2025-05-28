import java.io.*;
import java.net.*;

public class StatTDMServer {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java StatTDMServer <output1.txt> <output2.txt> ...");
            return;
        }

        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server is listening on port 5000...");
        Socket socket = serverSocket.accept();
        System.out.println("Client connected from " + socket.getInetAddress());

        DataInputStream in = new DataInputStream(socket.getInputStream());

        int N = args.length;
        FileOutputStream[] outputs = new FileOutputStream[N];
        for (int i = 0; i < N; i++) {
            outputs[i] = new FileOutputStream(args[i]);
        }

        int frameCounter = 1;

        while (true) {
            try {
                int frameSize = in.readInt();
                byte[] frame = new byte[frameSize];
                in.readFully(frame);

                System.out.print("Received Frame #" + frameCounter + ": ");
                for (int i = 0; i < frame.length; i += 2)
                    System.out.print("[" + frame[i] + ", " + (char) frame[i + 1] + "] ");
                System.out.println();

                for (int i = 0; i < frame.length; i += 2) {
                    int streamID = frame[i] - 1;
                    byte data = frame[i + 1];
                    if (streamID >= 0 && streamID < N) {
                        outputs[streamID].write(data);
                    }
                }

                frameCounter++;
            } catch (EOFException e) {
                break;
            }
        }

        for (FileOutputStream output : outputs)
            output.close();
        socket.close();
        serverSocket.close();
        System.out.println("Server closed.");
    }
}
