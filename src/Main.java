import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    //java Sender <file_path> <receiver_port> <window_size_N> <retransmission_timeout>

    private static final String HOSTNAME = "127.0.0.1";

    public static void main(String[] args) {
        String fp = args[0];
        int port = Integer.parseInt(args[1]);
        int N = Integer.parseInt(args[2]);
        int timeout = Integer.parseInt(args[3]);

        System.out.println("FP: " + fp);
        System.out.println("Port: " + port);
        System.out.println("N: " + N);
        System.out.println("Timeout: " + timeout);

        try {
            byte[] fileData = Files.readAllBytes(Paths.get(fp));

            DatagramSocket datagramSocket = new DatagramSocket();

            ReceiveManager receiveManager = new ReceiveManager(datagramSocket, HOSTNAME, port);
            SendManager sendManager = new SendManager(fileData, datagramSocket, HOSTNAME, port, N);
            TimeManager timeManager = new TimeManager(timeout);

            sendManager.setTimeManager(timeManager);
            timeManager.setSendManager(sendManager);
            receiveManager.setTimeManager(timeManager);

            receiveManager.start();
            sendManager.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}