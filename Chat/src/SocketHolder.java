
import java.net.Socket;

// TODO: Auto-generated Javadoc
/**
 * The Class SocketHolder.
 * Klasa zapewnia globalne przechowanie wartości Socketa, na którym ma się opierać połączenie klient-serwer.
 * Zapewnia rowniez, że na tym samaym IP nie zostanie otwarty ponownie kolejny Socket.
 */
public class SocketHolder {
    
    /** The socket. */
    private static Socket socket;

    /**
     * Gets the socket.
     *
     * @return the socket
     */
    public static synchronized Socket getSocket() {
        return socket;
    }

    /**
     * Sets the socket.
     *
     * @param socket the new socket
     */
    public static synchronized void setSocket(Socket socket) {
        if (SocketHolder.socket != null && !SocketHolder.socket.isClosed()) {
            throw new IllegalStateException("Socket is already open");
        }
        SocketHolder.socket = socket;
    }
        
}
