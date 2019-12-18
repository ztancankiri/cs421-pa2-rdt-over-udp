import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimeManager extends Thread {

    private AtomicBoolean isActive;
    private ArrayList<Packet> timerList;
    private int timeoutLimit;
    private final int SLEEP_AMOUNT = 1;
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
        timerList.add(new Packet(sequenceNo, timeoutLimit, 0, 0));
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

}
