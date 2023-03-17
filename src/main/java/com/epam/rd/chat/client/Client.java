package com.epam.rd.chat.client;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class Client {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "2099";

    public static void main(String...args) {
        Options options =
            new Options()
            .addOption("h", true, "Host name")
            .addOption("p", true, "Port number");

        CommandLineParser clp = new DefaultParser();

        try {

            CommandLine cl = clp.parse(options, args);

            String hostServer = cl.getOptionValue("h",
                                                  DEFAULT_HOST);
            int portServer = Integer.parseInt(cl.getOptionValue("p",
                                                                DEFAULT_PORT));
            ChatClient cc = new ChatClient(hostServer,
                                           portServer);
            System.out.println("connecting");
            cc.connect("I'm the walrus");
            System.out.println("status");
            cc.status("Writing");
            System.out.println("message 1");
            cc.message("This is a short message");
            System.out.println("message 2");
            cc.message("This is another short message");
            System.out.println("disconnect");
            cc.disconnect();
        } catch(ParseException pe) {
            System.err.println("Invalid option: " + pe);
            System.exit(1);
        } catch (UnknownHostException uhe) {
            System.err.println("Unknown Host Name Exception: " + uhe);
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
            System.exit(1);
        }


    }
}
