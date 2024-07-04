/*
 * 
 */

import javax.crypto.SecretKey;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.SwingUtilities;
import java.security.KeyPair;
import java.security.PublicKey;

// TODO: Auto-generated Javadoc
/**
 * The Class Client.
 * Klasa odpowiedzialna jest za nawiązanie połączenia z serwerem, przygotowanie wszystkich zmiennych 
 * potrzebnych do protokołu Diffiego-Hellmana.
 */
public class Client {
    
    /** The aes key. */
    private static SecretKey aesKey;

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
            // Nasłuchiwanie IP serwera.
            String serverAddress = ClientDiscovery.discoverServer();
            int port = 2137;
            System.out.println(serverAddress);
            
            //Otworzenie Socketa od strony klienta.
            Socket serverSocket = new Socket(serverAddress, port);
            SocketHolder.setSocket(serverSocket);

            ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream());

            System.out.println("Connected to server.");
            
            //Przyjęcie klucza publicznego serwera
            byte[] serverPublicKeyBytes = (byte[]) in.readObject();
            PublicKey serverPublicKey = DHUtil.decodePublicKey(serverPublicKeyBytes);

            //Wygenerowanie pary kluczy
            KeyPair keyPair = DHUtil.generateDHKeyPair();
            out.writeObject(keyPair.getPublic().getEncoded());
            out.flush();
            
            //Wygenerowanie wspólnego sekretu.
            byte[] sharedSecret = DHUtil.generateSharedSecret(keyPair.getPrivate(), serverPublicKey);
            
            //Wygenerowanie klucza i uruchomienie GUI
            aesKey = DHUtil.createAESKeyFromSharedSecret(sharedSecret);
            System.out.println(keyPair);

            SwingUtilities.invokeLater(() -> new Chat("Client", serverSocket, aesKey, out, in));

        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
