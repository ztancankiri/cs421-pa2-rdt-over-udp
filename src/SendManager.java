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
        int counter = 1;
        while (isActive.get()) {
            sendPacket(counter);
            counter++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPacket(int seqNo) {
        try {
            byte[] data = new byte[1024];

            data[0] = (byte) ((seqNo >> 8) & 0xff);
            data[1] = (byte) (seqNo & 0xff);

            for (int i = 0; i < 1022; i++) {
                data[i + 2] = inputFileData[i];
            }

            socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(hostname), port));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // timeManager.addTimer(0);
    }

    public void onTimeout(int sequenceNo) {
        sendPacket(sequenceNo);
    }

    public void setActive(boolean value) {
        this.isActive.set(value);
    }

    public void setTimeManager(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    public void ackPacket(int sequenceNo) {
        // TODO: acknowledge that packet
    }
}
