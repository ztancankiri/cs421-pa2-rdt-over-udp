import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendManager extends Thread {

    private AtomicBoolean isActive;
    private TimeManager timeManager;
    private byte[] inputFileData;
    private DatagramSocket socket;
    private String hostname;
    private int port;

    public SendManager(byte[] inputFileData, DatagramSocket socket, String hostname, int port) {
        this.isActive = new AtomicBoolean(false);
        this.inputFileData = inputFileData;
        this.socket = socket;
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public synchronized void start() {
        super.start();
        setActive(true);
    }

    @Override
    public void run() {
        while (isActive.get()) {
            // TODO: sendPacket(..) according to Sliding Window logic.
        }
    }

    private void sendPacket(int seqNo, int index, int length) {
        try {
            byte[] data = new byte[1024];

            // Header of the packet
            data[0] = (byte) ((seqNo >> 8) & 0xff);
            data[1] = (byte) (seqNo & 0xff);

            for (int i = 0; i < length; i++) {
                data[i + 2] = inputFileData[index + i];
            }

            socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(hostname), port));
            timeManager.addPacket(seqNo, index, length);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public void onTimeout(Packet packet) {
        sendPacket(packet.sequenceNo, packet.index, packet.length);
    }

    public void setActive(boolean value) {
        this.isActive.set(value);
    }

    public void setTimeManager(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    public void ackPacket(int seqNo) {
        // TODO: acknowledge that packet with Sliding Window logic
    }
}
