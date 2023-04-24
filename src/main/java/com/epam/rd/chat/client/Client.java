package com.epam.rd.chat.client;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class Client {

    public static void main(String...args) {

        try {
            ChatClientControl ccc = new ChatClientControl(args);
            IChatClient cc = ccc.getIChatClient();
            ChatWindow cw = new ChatWindow(cc, ccc);
        } catch(IllegalArgumentException iae) {
            System.err.println("Invalid option: " + iae);
            System.exit(1);
        } catch (UnknownHostException uhe) {
            System.err.println("Unknown Host Name Exception: " + uhe);
            System.exit(1);
        } catch (SecurityException se) {
            System.err.println("Security exception: " + se);
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe);
            System.exit(1);
        }
    }
}
