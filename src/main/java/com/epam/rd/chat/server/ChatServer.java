package com.epam.rd.chat.server;

// import java.io.InputStreamReader;
// import java.io.OutputStreamWriter;
// import java.io.BufferedReader;
// import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;

public class ChatServer implements Runnable {

    private static final int CHATPORT = 2099;

    private ChatService chatService;
    private ServerSocket serverSocket;
    private boolean running;

    public ChatServer(ChatService chatService, int port) throws IOException {
        this.chatService = chatService;
        this.serverSocket = new ServerSocket(port);
        this.running = true;
    }

    public void run() {
        boolean running = true;
        while (running) {
            try {
                Socket chatClient = serverSocket.accept();
                new StubChatService(chatService,
                                    chatClient);
            } catch (IOException ioe) {
                System.err.println("Exception main ServerSocket: " +
                                   ioe);
                running = false;
            }
        }
    }

    public void stop() {
        running = false;
    }

    public static void main(String args[]) {

        ChatServer chatServer = null;
        try {
            ChatService chatService = new ChatService();
            chatServer = new ChatServer(chatService,
                                        CHATPORT);
            chatServer.run();
        }
        catch (IOException ioe) {
            System.err.println("Main exception: " + ioe);
            chatServer.stop();
        }
    }
}
