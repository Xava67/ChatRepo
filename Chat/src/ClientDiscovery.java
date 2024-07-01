import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientDiscovery {
    private static final int BROADCAST_PORT = 2137;
    private static final int BUFFER_SIZE = 1024;

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
