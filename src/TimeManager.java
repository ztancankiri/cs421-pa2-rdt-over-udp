import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimeManager extends Thread {

    private final int SLEEP_AMOUNT = 1; // 1 ms

    private AtomicBoolean isActive;
    private ArrayList<Packet> packetList;
    private int timeoutLimit;
    private SendManager sendManager;

    public TimeManager(int timeoutLimit) {
        this.isActive = new AtomicBoolean(false);
        this.packetList = new ArrayList<>();
        this.timeoutLimit = timeoutLimit;
    }

    @Override
    public synchronized void start() {
        super.start();
        setActive(true);
    }

    @Override
    public void run() {
        while (isActive.get()) {
            for (int i = 0; i < packetList.size(); i++) {
                packetList.get(i).increaseCounter();

                if (packetList.get(i).isTimeout()) {
                    sendManager.onTimeout(packetList.remove(i));
                    // .remove(index) returns the removed object.
                }
            }

            try {
                Thread.sleep(SLEEP_AMOUNT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPacket(int seqNo, int index, int length) {
        packetList.add(new Packet(seqNo, timeoutLimit, index, length));
    }

    public void stopTimer(int sequenceNo) {
        for (int i = 0; i < packetList.size(); i++) {
            if (packetList.get(i).sequenceNo == sequenceNo) {
                packetList.remove(i);
                sendManager.ackPacket(sequenceNo);
            }
        }
    }

    public void setSendManager(SendManager sendManager) {
        this.sendManager = sendManager;
    }

    public void setActive(boolean value) {
        this.isActive.set(value);
    }
}
