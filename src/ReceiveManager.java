import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReceiveManager extends Thread {

    private AtomicBoolean isActive;
    private TimeManager timeManager;
    private DatagramSocket socket;
    private String hostname;
    private int port;

    public ReceiveManager(DatagramSocket socket, String hostname, int port) {
        this.isActive = new AtomicBoolean(false);
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

            try {
                byte[] ackData = new byte[2];
                DatagramPacket receivePacket = new DatagramPacket(ackData, ackData.length, InetAddress.getByName(hostname), port);
                socket.receive(receivePacket);

                byte[] ackNoData = new byte[] { 0x00, 0x00, ackData[0], ackData[1] };
                int ackNo = ByteBuffer.wrap(ackNoData).getInt();

                stopTimer(ackNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopTimer(int sequenceNo) {
        timeManager.stopTimer(sequenceNo);
    }

    public void setTimeManager(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    public void setActive(boolean value) {
        this.isActive.set(value);
    }
}