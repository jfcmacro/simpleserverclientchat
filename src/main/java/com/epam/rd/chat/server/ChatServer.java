package com.epam.rd.chat.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class ChatServer implements Runnable {

    private static final String DEFAULT_PORT = "2099";

    private ChatService chatService;
    private ServerSocket serverSocket;
    private boolean running;

    public ChatServer(ChatService chatService, int port) throws IOException {
        this.chatService = chatService;
        this.serverSocket = new ServerSocket(port);
        this.running = true;
    }

    @Override
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

        Options options =
            new Options()
            .addOption("p", true, "Port number")
            .addOption("e", false, "Enable an echo server");

        CommandLineParser clp = new DefaultParser();

        ChatServer chatServer = null;
        try {

            CommandLine cl = clp.parse(options, args);

            int portServer = Integer.parseInt(cl.getOptionValue("p",
                                                                DEFAULT_PORT));
            boolean echoServer = cl.hasOption("e") ? true : false;

            ChatService chatService = new ChatService(echoServer);
            chatServer = new ChatServer(chatService,
                                        portServer);
            chatServer.run();
        }
        catch (ParseException pe) {
            System.err.println("Invalid option: " + pe);
            System.exit(1);
        }
        catch (IOException ioe) {
            System.err.println("Main exception: " + ioe);
            if (chatServer != null) chatServer.stop();
            System.exit(1);
        }
    }
}
