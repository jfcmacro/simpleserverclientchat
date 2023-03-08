package com.epam.rd.chat.server;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

class StubChatService implements Runnable {
    enum ClientState { DISCONNECTED, CONNECTED, OFFLINE };

    private static final String NACK = "NACK \n";
    private static final String ACK  = "ACK \n";
    private ChatService chatService;
    private Socket client;
    private BufferedReader bf;
    private BufferedWriter bw;
    private ClientState state;

    StubChatService(ChatService chatService,
                    Socket client) throws IOException {
        this.chatService = chatService;
        this.client = client;
        bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
        bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        state = ClientState.DISCONNECTED;
        (new Thread(this)).start();
    }

    private String processMsg(String msg) {
        int spaceIdx = msg.indexOf(' ');
        String msgDec[] = new String[2];
        msgDec[0] = msg.substring(0, spaceIdx);
        msgDec[1] = msg.substring(spaceIdx + 1);
        String reply = NACK;
        try {
            switch(msgDec[0].toUpperCase()) {
            case "CONNECT":
                if (state == ClientState.DISCONNECTED) {
                    if (chatService.connect(msgDec[1], this)) {
                        state = ClientState.CONNECTED;
                        reply = ACK;
                    }
                }
                break;
            case "STATUS":
                if (state == ClientState.CONNECTED) {
                    chatService.status(msgDec[1], this);
                    reply = ACK;
                }
                break;

            case "MESSAGE":
                if (state == ClientState.CONNECTED) {
                    chatService.sendMessage(msgDec[1], this);
                    reply = ACK;
                }
                break;
            case "DISCONNECT":
                if (state == ClientState.CONNECTED) {
                    chatService.disconnect(this);
                    reply = ACK;
                    state = ClientState.OFFLINE;
                }
                break;
            }
        }
        catch (IOException ieo) {
        }

        return reply;
    }

    private void request(String msg) throws IOException {
        bw.write(msg + "\n");
        bw.flush();
        // String reply = bf.readLine();
        // System.out.println(reply);
    }

    void connect(String client) throws IOException {
        request("CONNECT " + client);
    }

    void status(String status) throws IOException {
        request("STATUS " + status);
    }

    void sendMessage(String msg) throws IOException {
        request("MESSAGE " + msg);
    }

    void disconnect() throws IOException {
        request("DISCONNECT ");
    }

    public void run() {
        while (state != ClientState.OFFLINE) {
            try {
                String msg = bf.readLine();
                String replyMsg = processMsg(msg);
                bw.write(replyMsg);
                bw.flush();
            }
            catch (IOException ioe) {
                state = ClientState.OFFLINE;
            }
        }
        try {
            bf.close();
            bw.close();
            client.close();
        }
        catch (IOException ioe) { 
        }
    }
}
