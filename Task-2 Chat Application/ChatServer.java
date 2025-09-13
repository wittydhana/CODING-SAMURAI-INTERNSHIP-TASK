import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatServer {

    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        int port = 12345;
        System.out.println("Chat server started on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true); // auto-flush
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Ask for username
                out.println("Enter your username:");
                username = in.readLine();
                if (username == null || username.isEmpty()) {
                    username = "Anonymous";
                }

                // Add to client list before broadcasting
                synchronized (clientHandlers) {
                    clientHandlers.add(this);
                }

                // Broadcast join message
                broadcastMessage("[" + username + "] joined the chat!", null);
                System.out.println(username + " connected.");

                // Listen for messages
                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage(message, username);
                    System.out.println("[" + username + "]: " + message);
                }

            } catch (IOException e) {
                System.out.println("Connection error with " + username);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
                synchronized (clientHandlers) {
                    clientHandlers.remove(this);
                }
                broadcastMessage("[" + username + "] left the chat.", null);
                System.out.println(username + " disconnected.");
            }
        }

        private void broadcastMessage(String message, String sender) {
            String time = LocalTime.now().format(timeFormatter);
            String formattedMessage;

            if (sender == null) {
                // System messages like join/leave
                formattedMessage = "\u001B[33m[" + time + "] " + message + "\u001B[0m"; // Yellow
            } else {
                // Color-coded by sender hash
                int colorCode = 31 + Math.abs(sender.hashCode()) % 6; // 31-36 ANSI codes
                formattedMessage = "\u001B[" + colorCode + "m[" + time + "] [" + sender + "]: " + message + "\u001B[0m";
            }

            synchronized (clientHandlers) {
                for (ClientHandler client : clientHandlers) {
                    client.out.println(formattedMessage);
                }
            }
        }
    }
}
