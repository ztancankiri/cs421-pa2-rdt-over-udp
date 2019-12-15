import java.util.concurrent.atomic.AtomicBoolean;

public class ReceiveManager extends Thread {

    private AtomicBoolean isActive;
    private TimeManager timeManager;

    public ReceiveManager() {
        this.isActive = new AtomicBoolean(false);
    }

    @Override
    public synchronized void start() {
        super.start();
        isActive.set(true);
    }

    @Override
    public void run() {
        while (isActive.get()) {

        }
    }

    private void stopTimer(int sequenceNo) {
        timeManager.stopTimer(sequenceNo);
    }

    public void setTimeManager(TimeManager timeManager) {
        this.timeManager = timeManager;
    }
}
