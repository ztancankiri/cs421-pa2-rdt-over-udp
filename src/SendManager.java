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
    private int N;
    private int[] window;
    private int windowPoint;
    private int startIndex;
    private AtomicBoolean isFull;

    public SendManager(byte[] inputFileData, DatagramSocket socket, String hostname, int port, int N) {
        this.isActive = new AtomicBoolean(false);
        this.isFull = new AtomicBoolean(false);
        this.inputFileData = inputFileData;
        this.socket = socket;
        this.hostname = hostname;
        this.port = port;
        this.N = N;
        this.window = new int[N];
        for (int i = 0; i < N; i++)
            window[i] = -2;
        this.windowPoint = 0;
        this.startIndex = 0;
    }

    @Override
    public synchronized void start() {
        super.start();
        setActive(true);
    }

    @Override
    public void run() {
        int index = 0;
        while (isActive.get()) {
            if(!isFull.get()) {
                for (int i = 0; i < N; i++){
                    sendPacket(i, index++, inputFileData.length);
                   // ackPacket(i); pozitif ack geldiğinde çağırılıcak
                    //Burada neler dönüyor acaba ?
                }
            }
        }
    }

    private void sendPacket(int seqNo, int index, int length) {
        try {
            byte[] data = new byte[1024];

            // Header of the packet
            data[0] = (byte) ((seqNo >> 8) & 0xff);
            data[1] = (byte) (seqNo & 0xff);

            for(int i = 0; i < length; i++) {
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
        if(windowPoint == N - 1 )
        {
            windowPoint = 0;
        }
        window[windowPoint] = seqNo;
        windowPoint++;
        for (int i = 0; i < N; i++)
        {
            if( window[i] == -2) {
                startIndex = i; // first unacknowledgement number
                break;
            }
        }
        if(windowPoint == startIndex) {
            System.out.println("FULL WİNDOW");
            isFull.getAndSet(true);
        }
    }

}
