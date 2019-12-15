import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {
    //java Sender <file_path> <receiver_port> <window_size_N> <retransmission_timeout>

    public static void main(String[] args) {
        String fp = args[0];
        int port = Integer.parseInt(args[1]);
        int N = Integer.parseInt(args[2]);
        int timeout = Integer.parseInt(args[3]);

        System.out.println("FP: " + fp);
        System.out.println("Port: " + port);
        System.out.println("N: " + N);
        System.out.println("Timeout: " + timeout);

//        DatagramSocket datagramSocket = null;
//
//        try {
//            datagramSocket = new DatagramSocket(port, InetAddress.getLocalHost());
//            System.out.println("Connected");
//        }
//        catch (Exception e) {
//            System.out.println("Exception: " + e.getMessage());
//            System.exit(1);
//        }


    }
}