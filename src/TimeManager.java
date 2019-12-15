import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimeManager extends Thread {

    private AtomicBoolean isActive;
    private ArrayList<PacketTimer> timerList;
    private int timeoutLimit;
    private final int SLEEP_AMOUNT = 0;
    private SendManager sendManager;

    public TimeManager(int timeoutLimit) {
        this.isActive = new AtomicBoolean(false);
        this.timerList = new ArrayList<>();
        this.timeoutLimit = timeoutLimit;
    }

    @Override
    public synchronized void start() {
        super.start();
        isActive.set(true);
    }

    @Override
    public void run() {
        while (isActive.get()) {
            for (int i = 0; i < timerList.size(); i++) {
                timerList.get(i).increaseCounter();

                if (timerList.get(i).isTimeout()) {
                    int seqNo = timerList.get(i).sequenceNo;
                    timerList.remove(i);
                    sendManager.onTimeout(seqNo);
                }
            }

            try {
                Thread.sleep(SLEEP_AMOUNT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addTimer(int sequenceNo) {
        timerList.add(new PacketTimer(sequenceNo, timeoutLimit));
    }

    public void stopTimer(int sequenceNo) {
        for (int i = 0; i < timerList.size(); i++) {
            if (timerList.get(i).sequenceNo == sequenceNo) {
                timerList.remove(i);
                sendManager.ackPacket(sequenceNo);
            }
        }
    }

    public void setSendManager(SendManager sendManager) {
        this.sendManager = sendManager;
    }

    private class PacketTimer {

        private int sequenceNo;
        private int counter;
        private int limit;

        private PacketTimer(int sequenceNo, int limit) {
            this.sequenceNo = sequenceNo;
            this.limit = limit;
            this.counter = 0;
        }

        private boolean isTimeout() {
            return counter >= limit;
        }

        private void increaseCounter() {
            counter++;
        }
    }
}
