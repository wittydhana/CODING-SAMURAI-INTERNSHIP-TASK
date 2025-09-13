import java.io.*;
import java.net.*;

public class ChatClient {

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;

        try {
            Socket socket = new Socket(hostname, port);

            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Read server prompt for username
            System.out.println(serverIn.readLine());
            String username = console.readLine();
            serverOut.println(username);

            System.out.println("Connected to chat server as " + username);

            // Thread to read messages from server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = serverIn.readLine()) != null) {
                        System.out.println(message); // Already formatted with color/timestamp
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            // Thread to send messages to server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = console.readLine()) != null) {
                        serverOut.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Connection error.");
                }
            }).start();

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
