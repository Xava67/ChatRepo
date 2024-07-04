
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerBroadcast.
 * Klasa odpowiedzialna jest za rozgłaszanie po sieci lokalnej adresu IP serwera, w celu otworzenia ServerSocketa, 
 * dopóki nie nastapi połączenie z klientem.
 */
public class ServerBroadcast implements Runnable {
    
    /** The Constant BROADCAST_PORT. */
    private static final int BROADCAST_PORT = 2137;
    
    /** The Constant BROADCAST_INTERVAL. */
    private static final int BROADCAST_INTERVAL = 500; // milliseconds

    /**
     * Run.
     */
    @Override
    public synchronized void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            InetAddress localHost = InetAddress.getLocalHost();
            String serverIP = localHost.getHostAddress();
            System.out.println(serverIP);
            byte[] buffer = serverIP.getBytes();

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), BROADCAST_PORT);
                socket.send(packet);
                Thread.sleep(BROADCAST_INTERVAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
