import java.util.concurrent.atomic.AtomicBoolean;

public class SendManager extends Thread {

    private AtomicBoolean isActive;
    private TimeManager timeManager;
    private byte[] inputFileData;

    public SendManager(byte[] inputFileData) {
        this.isActive = new AtomicBoolean(false);
        this.inputFileData = inputFileData;
    }

    @Override
    public synchronized void start() {
        super.start();
        isActive.set(true);
    }

    @Override
    public void run() {
        while (isActive.get()) {
            sendPacket();
        }
    }

    private void sendPacket() {



        // timeManager.addTimer(0);
    }

    public void onTimeout(int sequenceNo) {
        // TODO: Send the packet with this seqNo again.
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
