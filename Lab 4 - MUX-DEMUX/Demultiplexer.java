import java.io.*;
import java.net.*;

public class Demultiplexer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server is listening on port 5000...");
        Socket socket = serverSocket.accept();
        System.out.println("Client connected from " + socket.getInetAddress() + ":" + socket.getPort());

        DataInputStream in = new DataInputStream(socket.getInputStream());

        String[] outputFiles = {"output1.txt", "output2.txt", "output3.txt"};
        int T = 30, N = 3;
        FileOutputStream[] outputs = new FileOutputStream[N];

        for (int i = 0; i < N; i++)
            outputs[i] = new FileOutputStream(outputFiles[i]);

        byte[] packet = new byte[N * T];
        int bytesRead;

        while (true) {
            int totalRead = 0;
            while (totalRead < packet.length) {
                bytesRead = in.read(packet, totalRead, packet.length - totalRead);
                if (bytesRead == -1) {
                    System.out.println("Client disconnected.");
                    break;
                }
                totalRead += bytesRead;
            }
            if (totalRead == 0) break; 

            System.out.print("Received Packet: ");
            for (int k = 0; k < totalRead; k++) {
                System.out.print((char) packet[k] + " ");
            }
            System.out.println();

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < T; j++) {
                    byte b = packet[i * T + j];
                    if (b != '#')
                        outputs[i].write(b);
                }
            }
        }

        for (int i = 0; i < N; i++)
            outputs[i].close();

        socket.close();
        serverSocket.close();
        System.out.println("Server closed.");
    }
}
