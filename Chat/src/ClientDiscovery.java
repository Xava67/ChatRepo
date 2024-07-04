import java.net.DatagramPacket;
import java.net.DatagramSocket;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientDiscovery.
 * Klasa odpowiedzialna jest za zwrócenie rozgłasanego przez serwer adresu IP, aby klient mógł utworzyć
 * Socketa. Po uruchomienu klienta i połączeniu, adres IP serwera przestaje być rozgłaszany.
 */
public class ClientDiscovery {
    
    /** The Constant BROADCAST_PORT. */
    private static final int BROADCAST_PORT = 2137;
    
    /** The Constant BUFFER_SIZE. */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Discover server.
     *
     * @return the string
     * @throws Exception the exception
     */
    public synchronized static String discoverServer() throws Exception {
        try (DatagramSocket socket = new DatagramSocket(BROADCAST_PORT)) {
            socket.setBroadcast(true);
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);
                String serverIP = new String(packet.getData(), 0, packet.getLength());
                System.out.println(serverIP);
                return serverIP;
            }
        }
    }
}
