import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private final String HOSTNAME = "127.0.0.1";
    private byte[] fileData;
    private DatagramSocket socket;
    private int port;
    private int N;
    private int timeout;
    private int lastAck;

    private Packet[] packets;
    private int windowBase;
    private int noOfPackets;

    private int ackCounter;

    public Main(String filePath, int port, int N, int timeout) {
        this.port = port;
        this.N = N;
        this.timeout = timeout;
        this.ackCounter = 0;
        this.lastAck = -1;

        try {
            fileData = Files.readAllBytes(Paths.get(filePath));
            System.out.println("FileSize: " + fileData.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double size = fileData.length;
        double packet = 1022;
        double result = size / packet;
        this.noOfPackets = (int) Math.ceil(result);
        System.out.println("#Packets: " + noOfPackets);

        this.packets = new Packet[noOfPackets];
        for (int i = 0; i < noOfPackets; i++)
            packets[i] = null;

        this.windowBase = 0;

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        sendWindow();

        while (ackCounter < noOfPackets) {
            try {
                byte[] ackData = new byte[2];
                DatagramPacket receivePacket = new DatagramPacket(ackData, ackData.length, InetAddress.getByName(HOSTNAME), port);
                socket.receive(receivePacket);

                byte[] ackNoData = new byte[] { 0x00, 0x00, ackData[0], ackData[1] };
                int ackNo = ByteBuffer.wrap(ackNoData).getInt();

                ackPacket(ackNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sendLastPacket(noOfPackets + 1);
    }

    public void ackPacket(int sequenceNo) {
        int seqIndex = sequenceNo - 1;

        if (seqIndex >= windowBase && seqIndex < windowBase + N) {
            if (packets[seqIndex] != null) {
                packets[seqIndex].interrupt();
                packets[seqIndex] = null;
                ackCounter++;

                if (seqIndex > lastAck)
                    lastAck = seqIndex;

                int counter = 0;
                for (int i = windowBase; i < lastAck; i++) {
                    if (packets[i] == null) {
                        counter++;
                    }
                }

                if (counter == lastAck - windowBase) {
                    windowBase = lastAck + 1;
                    lastAck = -1;
                    sendWindow();
                }
            }
        }
    }

    public void sendWindow() {
        for (int i = windowBase; i < windowBase + N && i < noOfPackets; i++) {
            if (packets[i] == null) {
                packets[i] = new Packet(i + 1, socket, HOSTNAME, port, fileData, timeout);
                packets[i].start();
            }
        }
    }

    private void sendLastPacket(int seqNo) {
        try {
            byte[] data = new byte[2];
            data[0] = (byte) 0;
            data[1] = (byte) 0;
            socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(HOSTNAME), port));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public class Packet extends Thread {

        public static final int PACKET_DATA_SIZE = 1022;

        public int sequenceNo;
        private DatagramSocket socket;
        private String hostname;
        private int port;
        private byte[] fileData;
        private int timeout;

        public Packet(int sequenceNo, DatagramSocket socket, String hostname, int port, byte[] fileData, int timeout) {
            this.sequenceNo = sequenceNo;
            this.socket = socket;
            this.hostname = hostname;
            this.port = port;
            this.fileData = fileData;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    sendPacket();
                    Thread.sleep(timeout);
                }
            }
            catch (InterruptedException e) {
                return;
            }
        }

        private void sendPacket() {
            int size = fileData.length;

            int index = (sequenceNo - 1) * PACKET_DATA_SIZE;
            int length = Math.min(size - index, PACKET_DATA_SIZE);

            try {
                byte[] data = new byte[length + 2];

                // Header of the packet
                data[0] = (byte) ((sequenceNo >> 8) & 0xff);
                data[1] = (byte) (sequenceNo & 0xff);

                for(int i = 0; i < length; i++) {
                    data[i + 2] = fileData[index + i];
                }

                socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(hostname), port));
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        String fp = args[0];
        int port = Integer.parseInt(args[1]);
        int N = Integer.parseInt(args[2]);
        int timeout = Integer.parseInt(args[3]);

        System.out.println("FP: " + fp);
        System.out.println("Port: " + port);
        System.out.println("N: " + N);
        System.out.println("Timeout: " + timeout);

        new Main(fp, port, N, timeout);
    }
}