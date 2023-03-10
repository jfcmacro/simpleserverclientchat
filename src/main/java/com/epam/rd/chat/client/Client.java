package com.epam.rd.chat.client;

import java.io.IOException;
import java.net.UnknownHostException;

// import org.apache.commons.cli;
public class Client {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 2099;
    public static void main(String...args) {

        try {
            StubChatClient scc = new StubChatClient(DEFAULT_HOST,
                                                    DEFAULT_PORT);
            scc.connect("I'm the walrus");
            scc.status("Writing");
            scc.sendMessage("This is a short message");
            scc.sendMessage("This is another short message");
            scc.disconnect();
        } catch (UnknownHostException uhe) {
            System.err.println("Unknown Host Name Exception: " + uhe);
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
            System.exit(1);
        }


    }
}
