import java.io.*;
import java.net.*;

public class ClientTwoWayBit {
    public static String bitDestuff(String stuffed) {
        StringBuilder destuffed = new StringBuilder();
        int count = 0;
        for (int i = 0; i < stuffed.length(); i++) {
            char bit = stuffed.charAt(i);
            if (bit == '1') {
                count++;
                destuffed.append('1');
                if (count == 5 && i + 1 < stuffed.length() && stuffed.charAt(i + 1) == '0') {
                    i++; // skip stuffed bit
                    count = 0;
                }
            } else {
                destuffed.append('0');
                count = 0;
            }
        }
        return destuffed.toString();
    }

    public static String bitStuff(String input) {
        StringBuilder stuffed = new StringBuilder();
        int count = 0;
        for (char bit : input.toCharArray()) {
            if (bit == '1') {
                count++;
                stuffed.append('1');
                if (count == 5) {
                    stuffed.append('0'); // stuff a 0 after 5 1s
                    count = 0;
                }
            } else {
                stuffed.append('0');
                count = 0;
            }
        }
        return stuffed.toString();
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000); 
        // For Client in diffent PC "localhost" is replaced by "IP Address"
        // IP Address of this PC : 192.168.0.139
        System.out.println("Connected to Server at port: " + socket.getPort());

        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        Thread readThread = new Thread(() -> {
            try {
                String msg;
                while (true) {
                    msg = input.readUTF();
                    if (msg.equalsIgnoreCase("stop")) {
                        System.out.println("Server terminated the connection.");
                        System.exit(0);
                    }
                    System.out.println("Stuffed from Server: " + msg);
                    System.out.println("Destuffed: " + bitDestuff(msg));
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        });

        Thread writeThread = new Thread(() -> {
            try {
                String msg;
                while (true) {
                    System.out.print("Enter binary data to send: ");
                    msg = console.readLine();
                    if (msg.equalsIgnoreCase("stop")) {
                        output.writeUTF("stop");
                        System.out.println("Client terminated the connection.");
                        System.exit(0);
                    }
                    String stuffed = bitStuff(msg);
                    output.writeUTF(stuffed);
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        });

        readThread.start();
        writeThread.start();
    }
}
