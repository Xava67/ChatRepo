import javax.crypto.SecretKey;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.SwingUtilities;
import java.security.KeyPair;
import java.security.PublicKey;

public class Client {
    private static SecretKey aesKey;

    public static void main(String[] args) {
        try {
            // Discover the server IP address
            String serverAddress = ClientDiscovery.discoverServer();
            int port = 2137;
            System.out.println(serverAddress);

            Socket serverSocket = new Socket(serverAddress, port);
            SocketHolder.setSocket(serverSocket);

            ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());

            System.out.println("Connected to server.");

            byte[] serverPublicKeyBytes = (byte[]) in.readObject();
            PublicKey serverPublicKey = DHUtil.decodePublicKey(serverPublicKeyBytes);

            KeyPair keyPair = DHUtil.generateDHKeyPair();
            out.writeObject(keyPair.getPublic().getEncoded());
            out.flush();

            byte[] sharedSecret = DHUtil.generateSharedSecret(keyPair.getPrivate(), serverPublicKey);

            aesKey = DHUtil.createAESKeyFromSharedSecret(sharedSecret);
            System.out.println(keyPair);

            SwingUtilities.invokeLater(() -> new Chat("Client", serverSocket, aesKey, out, in));

        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
