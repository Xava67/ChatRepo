import java.io.IOException;
import java.net.Socket;

public class SocketHolder {
    private static Socket socket;

    public static synchronized Socket getSocket() {
        return socket;
    }

    public static synchronized void setSocket(Socket socket) {
        if (SocketHolder.socket != null && !SocketHolder.socket.isClosed()) {
            throw new IllegalStateException("Socket is already open");
        }
        SocketHolder.socket = socket;
    }
    
    public static synchronized boolean isSocketOpen() {
        return socket != null && !socket.isClosed();
    }

    public static synchronized void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }
}
