package com.epam.rd.chat.client;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

class StubChatClient implements Runnable {

    private Socket socket;
    private BufferedReader bf;
    private BufferedWriter bw;
    private static final String CONNECT = "CONNECT ";
    private static final String STATUS = "STATUS ";
    private static final String MESSAGE = "MESSAGE ";
    private static final String DISCONNECT = "DISCONNECT ";
    private static final String EOL = "\n";
    private boolean running;

    StubChatClient(String host, int port)
        throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        running = true;
        (new Thread(this)).start();
    }

    private void send(String message) throws IOException {
        bw.write(message + EOL);
        bw.flush();
    }

    private void processMsg(String msg) {
        int spaceIdx = msg.indexOf(' ');
        String msgDes[] = new String[2];
        msgDes[0] = msg.substring(0, spaceIdx);
        msgDes[1] = msg.substring(spaceIdx + 1);
        switch(msgDes[0].toUpperCase()) {
        case "CONNECT":
            System.out.println("User connected: " + msgDes[1]);
            break;

        case "STATUS":
            System.out.println("Status: " + msgDes[1]);
            break;

        case "MESSAGE":
            System.out.println("Message: " + msgDes[1]);
            break;

        case "DISCONNECT":
            running = false;
            break;

        case "ACK":
            System.out.println("ACK received");
            break;

        case "NACK":
            System.out.println("NACK received");
            break;
        }
    }

    public void receive() throws IOException {
        String reply = bf.readLine();
    }

    public boolean connect(String name) throws IOException {
        send(CONNECT + name);
        return false;
    }

    public void status(String status) throws IOException {
        send(STATUS + status);
    }

    public void sendMessage(String msg) throws IOException {
        send(MESSAGE + msg);
    }

    public void disconnect() throws IOException {
        send(DISCONNECT);
    }

    public void run() {
        while (running) {
            try { 
                String msg = bf.readLine();
                processMsg(msg);
            } catch (IOException ioe) {
                running = false;
            }
        }
        try {
            bf.close();
            bw.close();
            socket.close();
        } catch (IOException ioe) { }
    }
}
