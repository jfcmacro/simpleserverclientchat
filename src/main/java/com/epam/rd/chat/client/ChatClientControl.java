package com.epam.rd.chat.client;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Handler;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class ChatClientControl {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "2099";
    private static final String DEFAULT_LOGFILE = "client.log";
    private static final String DEFAULT_LOGPROP = "client-logging.properties";
    static Logger logger = null;
    private String hostServer = null;
    private int portServer;
    private String clientName = "I'm the walrus";
    private IChatClient chatClient = null;
    private boolean loggingEnable = false;

    public ChatClientControl(String...args)
        throws UnknownHostException, IOException, SecurityException {
        try {
            Options options =
                new Options()
                .addOption("h", true, "Host name")
                .addOption("l", false, "log file")
                .addOption("p", true, "Port number")
                .addOption("u", true, "User name");

            CommandLineParser clp = new DefaultParser();

            CommandLine cl = clp.parse(options, args);

            hostServer = cl.getOptionValue("h",
                                           DEFAULT_HOST);

            portServer = Integer.parseInt(cl
                                          .getOptionValue("p",
                                                          DEFAULT_PORT));

            if (cl.hasOption("l")) {
                LogManager
                    .getLogManager()
                    .readConfiguration(new FileInputStream(DEFAULT_LOGPROP));
                ChatClientControl.logger =
                    Logger.getLogger(ChatClientControl.class.getName());
                logger.addHandler(new FileHandler(DEFAULT_LOGFILE));
            }

            clientName = cl.getOptionValue("u",
                                           clientName);

            chatClient = new ChatClient(hostServer, portServer);
            if (logger != null) {
                logger.info("Connecting");
            }
            chatClient.connect(clientName);
        }
        catch (ParseException pe) {
            throw new IllegalArgumentException("Invalid argument option: " + pe);
        }
    }

    public IChatClient getIChatClient() {
       return chatClient;
    }

    // public void connect() throws IOException {
    //     chatClient.connect(clientName);
    // }

    public void sendMessage(String msg) throws IOException {
        if (logger != null) {
            logger.info("Send message: " + msg);
        }
        chatClient.sendMessage(msg);
    }

    public void disconnect() throws IOException {
        chatClient.disconnect();
    }

    // public String getHostServer() {
    //     return hostServer;
    // }

    // public int getPortServer() {
    //     return portServer;
    // }
}
