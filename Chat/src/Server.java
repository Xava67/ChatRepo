
import javax.crypto.SecretKey;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.SwingUtilities;
import java.security.KeyPair;
import java.security.PublicKey;
import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class Server.
 * Klasa Server, odpowiedzialna jest za rozpoczecie połączenia oraz za nasłuchiwanie odpowiedzi od klienta,
 * jeżeli ten odpowie (uruchomi swoją aplikację), następuje wymiana kluczy i zainicjowanie komunikacji.
 */
public class Server {
    
    /** The Constant PORT. */
    private static final int PORT = 2137;
    
    /** The server socket. */
    private static ServerSocket serverSocket;

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // Rozgłaszanie adresu IP.
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
    
    //

    /**
     * Handle client.
     * nasłuchiwanie odpowiedzi klienta; Wygenerowanie pary kluczy w protokole Diffiego-Hellmana; Przjęcie klucza od klienta;
     * Wygenerowanie wspólnego sekretu; Wygenerowanie klucza AES na podstawie wspólnego sekretu; Uruchomienie GUI 
     *
     * @param clientSocket the client socket
     */
    private static void handleClient(Socket clientSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Client connected.");

            // 
            KeyPair keyPair = DHUtil.generateDHKeyPair();
            out.writeObject(keyPair.getPublic().getEncoded());
            out.flush();

            // 
            byte[] clientPublicKeyBytes = (byte[]) in.readObject();
            PublicKey clientPublicKey = DHUtil.decodePublicKey(clientPublicKeyBytes);

            // 
            byte[] sharedSecret = DHUtil.generateSharedSecret(keyPair.getPrivate(), clientPublicKey);

            // 
            SecretKey aesKey = DHUtil.createAESKeyFromSharedSecret(sharedSecret);
            System.out.println(keyPair);

            // 
            SwingUtilities.invokeLater(() -> new Chat("Server", clientSocket, aesKey, out, in));

        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the server socket.
     *
     * @return the server socket
     */
    public static ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Sets the server socket.
     *
     * @param serverSocket the new server socket
     */
    public static void setServerSocket(ServerSocket serverSocket) {
        Server.serverSocket = serverSocket;
    }
}
