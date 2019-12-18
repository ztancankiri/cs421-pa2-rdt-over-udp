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

    // Benim TODO yazdığım yerler dışında bir methoda dokunmadan yapabiliyor olman lazımr global
    // Class property si ekle canım onda bişi yok gerekiyorsa method da ekle ama benim yazdıgım methodlardan herhangi birinde
    // TODO lar hariç bir yere dokunmana gerek yok. Şuaonk timeout yeme halidne retransmission vs mevcut.
    // hiç düşünme oraları sen. sendPacket methoduna sadece bizima  şu inputFileData array i içerisindeki
    // o an gönderilcek paketin başlangıc index ini ve lengthini vericen data kısmının bak yalnız. 1022 gibi. header ı da
    // otomatik koyuyo şuan o method sen sadece headerı konacak seqNo yu vericen.
    //Ok ne kadar sürem var. Ne bilem bir tek bu kaldı. Bu logic eklenince çalışması lazım her şeyin :D
    // Bu arada proje böyle yapılır :D Farkındaysan metholar yazdım sen sadece signature kullanarak başka şeyler yapabilirsin.
    // Bana ihtiyacın yok mesela :D Evet ama tüm yapıyı sen kurduğun için bir yerde patlıycam :D Olum ihtiyacın olan methodları söyledim işte
    // onları kullanarak yapabilmen lazım :D Oki ben deniyim
    // Tamam kolay gelsin aşko xd sağoll sanadaaa

    public SendManager(byte[] inputFileData, DatagramSocket socket, String hostname, int port, int N) {
        this.isActive = new AtomicBoolean(false);
        this.inputFileData = inputFileData;
        this.socket = socket;
        this.hostname = hostname;
        this.port = port;
        this.N = N;
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
            // Burası bu threadin infinite loop u. Burda window u doldurana kadar send yapıcan.
            // window dolunca yer yoksa bişi yapmıcak. Yer acılınca yeni bişi yollucak.
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
    public void slidingWindow(int seqNo) {
        // TODO: acknowledge that packet with Sliding Window logic
    }
}
