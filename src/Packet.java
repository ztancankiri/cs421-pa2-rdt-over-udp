public class Packet {

    public int sequenceNo;
    public int timer;
    public int limit;
    public int index;
    public int length;

    public Packet(int sequenceNo, int limit, int index, int length) {
        this.sequenceNo = sequenceNo;
        this.limit = limit;
        this.timer = 0;
        this.index = index;
        this.length = length;
    }

    public boolean isTimeout() {
        return timer >= limit;
    }

    public void increaseCounter() {
        timer++;
    }
}