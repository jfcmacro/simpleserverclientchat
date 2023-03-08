package com.epam.rd.chat.server;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

class StubChatService implements Runnable {
    private ChatService chatService;
    private Socket client;
    private BufferedReader bf;
    private BufferedWriter bw;
    private boolean isRunning = true;

    StubChatService(ChatService chatService,
                    Socket client) throws IOException {
        this.chatService = chatService;
        this.client = client;
        bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
        bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        isRunning = true;
        (new Thread(this)).start();
    }

    private String processMsg(String msg) {
        String[] msgDec = msg.split(" ");
        String reply = "ACK \n";
        switch(msgDec[0].toUpperCase()) {
        case "CONNECT":
            reply = chatService.connect(msgDec[1], this) ?
                reply : "NACK \n";
            break;
        case "STATUS": chatService.status(msgDec[1], this);
            break;
        case "MESSAGE": chatService.sendMessage(msgDec[1], this);
            break;
        case "DISCONNECT": chatService.disconnect(this);
            isRunning = false;
            break;
        }

        return reply;
    }

    private void request(String msg) {
        bw.write(msg);
        bw.flush();
        String reply = bf.readLine();
        System.out.println(reply);
    }

    void connect(String client) {
        request("CONNECT " + client);
    }

    void status(String status) {
        request("STATUS " + status);
    }

    void sendMessage(String msg) {
        request("MESSAGE " + msg);
    }

    void disconnect() {
        request("DISCONNECT \n");
    }

    public void run() {
        while (isRunning) {
            String msg = bf.readLine();
            String replyMsg = processMsg(msg);
            bw.write(replyMsg);
            bw.flush();
        }
    }
}
