package Discarded;
import java.io.*;
import java.net.*;

public class SimpleServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, waiting for client...");
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connected.");
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    out.println("Echo: " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
