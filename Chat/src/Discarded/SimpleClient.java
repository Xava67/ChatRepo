package Discarded;
import java.io.*;
import java.net.*;

public class SimpleClient {
    public static void main(String[] args) {
        String serverAddress = "192.168.0.45";
        int port = 12345;

        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Connected to server.");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
                System.out.println("Server response: " + in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
