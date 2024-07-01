import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerBroadcast implements Runnable {
    private static final int BROADCAST_PORT = 2137;
    private static final int BROADCAST_INTERVAL = 500; // milliseconds

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
