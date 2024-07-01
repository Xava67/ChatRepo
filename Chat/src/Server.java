import javax.crypto.SecretKey;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.SwingUtilities;
import java.security.KeyPair;
import java.security.PublicKey;
import java.io.IOException;

public class Server {
    private static final int PORT = 2137;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        // Start broadcasting the server IP
        new Thread(new ServerBroadcast()).start();

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started, waiting for client...");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    SocketHolder.setSocket(clientSocket);
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Client connected.");

            // Generate Diffie-Hellman key pair
            KeyPair keyPair = DHUtil.generateDHKeyPair();
            out.writeObject(keyPair.getPublic().getEncoded());
            out.flush();

            // Receive client's public key
            byte[] clientPublicKeyBytes = (byte[]) in.readObject();
            PublicKey clientPublicKey = DHUtil.decodePublicKey(clientPublicKeyBytes);

            // Generate shared secret
            byte[] sharedSecret = DHUtil.generateSharedSecret(keyPair.getPrivate(), clientPublicKey);

            // Create AES key from shared secret
            SecretKey aesKey = DHUtil.createAESKeyFromSharedSecret(sharedSecret);
            System.out.println(keyPair);

            // Start the chat window
            SwingUtilities.invokeLater(() -> new Chat("Server", clientSocket, aesKey, out, in));

        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static void setServerSocket(ServerSocket serverSocket) {
        Server.serverSocket = serverSocket;
    }
}
