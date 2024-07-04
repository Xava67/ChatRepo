import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

// TODO: Auto-generated Javadoc
/**
 * Klasa odpowiedzialna jest za wyświetlanie GUI aplikacji, jak i również wysyłanie i odbieranie zaszyfrowanych
 * wiadomości.
 */
public class Chat extends JFrame {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1432098293678393253L;
    
    /** The chat area. */
    private JTextArea chatArea;
    
    /** The input field. */
    private JTextField inputField;
    
    /** The send button. */
    private JButton sendButton;
    
    /** The aes key. */
    private SecretKey aesKey;
    
    /** The out. */
    private ObjectOutputStream out;
    
    /** The in. */
    private ObjectInputStream in;
    
    /** The socket. */
    private Socket socket;
    
	/**
	 * Konstruktor pryjmuje tytuł, czyli nadawcę wiadomości wpisywanej w oknie, socket otwarty w klasach Client i Server
	 * oraz strumienie wiadomości przychodzących i wychodzących. * Po utworzeniu okna aplikacji, następuje wymiana wiadomości między klientem a serwerem.
	 *
	 * @param title the title
	 * @param socket the socket
	 * @param aesKey the aes key
	 * @param out the out
	 * @param in the in
	 */
	
    public Chat(String title, Socket socket, SecretKey aesKey, ObjectOutputStream out, ObjectInputStream in) {
        super(title);
        this.aesKey = aesKey;
        this.out = out;
        this.in = in;
        this.socket = SocketHolder.getSocket();

        chatArea = new JTextArea(20, 50);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        inputField = new JTextField(40);
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public synchronized void actionPerformed(ActionEvent e) {
                System.out.println(inputField.getText());
                sendMessage();
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public synchronized void actionPerformed(ActionEvent e) {
                System.out.println(inputField.getText());
                sendMessage();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        new Thread(new MessageListener()).start();
    }

    /**
	 * Proces wysyłania zaszyfrowanej wiadomości następuje po jej wpisaniu i wysłaniu za pomoca przycisku
	 * lub kliknięcia przycisku Enter. Polega on na szyfrowaniu wiadomości kluczem AES, wygenerowanym wcześniej
	 * w klasie wywołującej pojawienie się GUI i przekazanym do konstruktora. Następnie jest przekazywana sieciowo
	 * za pomoca Socketa do drugiego otworzonego okna.
	 */
	private synchronized void sendMessage() {
        try {
            System.out.println("Is socket either null or closed?");
            System.out.println("It is null: " + (socket == null));
            System.out.println("Closed: " + socket.isClosed());
            if (socket != null && !socket.isClosed()) {
                String message = inputField.getText();
                byte[] encryptedMessage = AESUtil.encrypt(aesKey, message);
                out.writeObject(encryptedMessage);
                out.flush();
                chatArea.append("Me: " + message + "\n");
                inputField.setText("");
            } else {
                chatArea.append("Error: Socket is closed. Cannot send message.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            chatArea.append("Error: " + e.getMessage() + "\n");
        }
    }
    
	/*
	 
	 */

    /**
	 * 
	 * Klasa wewnętrzna MessageListener "nasłuchuje" przychodzących wiadomości.
	 * Implementuje interfejs Runnable, czyli następuje ciągłe nasłuchiwanie wiadomości, dopóki połaczenie nie zostanie przerwane
	 * Odszyfrowanie wiadomości następuje znowu z wykorzystaniem klasy AESUtil i jej metody decrypt(key, message). 
	 * Następnie następuje jej wyświetlenie pod ostatnia wiadomością, lub na samym początku, jeżeli jest to pierwsza wiadomość.
	 *
	 * @see MessageEvent
	 */
	private class MessageListener implements Runnable {
        
        /**
         * Run.
         */
        @Override
        public synchronized void run() {
            try {
                System.out.println("MessageListener - is it working?");
                while (!socket.isClosed()) {
                    try {
                        System.out.println("Waiting for message...");
                        byte[] encryptedMessage = (byte[]) in.readObject();
                        System.out.println("Message received.");
                        String message = AESUtil.decrypt(aesKey, encryptedMessage);
                        System.out.println("Decrypted message: " + message);
                        chatArea.append("Friend: " + message + "\n");
                    } catch (EOFException | SocketException e) {
                        System.out.println("Connection closed: " + e.getMessage());
                        chatArea.append("Connection closed: " + e.getMessage() + "\n");
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        chatArea.append("Error: " + e.getMessage() + "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                chatArea.append("Error: " + e.getMessage() + "\n");
            }
        }
    }
}
