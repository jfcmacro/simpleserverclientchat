package com.epam.rd.chat.client;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

enum ACKTYPES { NO_ACK, ACK_WAITING, ACK_RECEIVED, NACK_RECEIVED };

class StubChatClient implements Runnable,
                                ChatClientService {
    private Socket socket;
    private BufferedReader bf;
    private BufferedWriter bw;
    private IChatClient chatClient;
    private static final String CONNECT = "CONNECT ";
    private static final String STATUS = "STATUS ";
    private static final String MESSAGE = "MESSAGE ";
    private static final String DISCONNECT = "DISCONNECT ";
    private static final String EOL = "\n";
    private boolean running;
    private Lock receiveLock;
    private Condition receivedAck;
    private ACKTYPES ackReceived;

    StubChatClient(IChatClient chatClient, String host, int port)
        throws UnknownHostException, IOException {
        this.chatClient = chatClient;
        this.socket = new Socket(host, port);
        this.bf =
            new BufferedReader(new InputStreamReader(socket
                                                     .getInputStream()));
        this.bw =
            new BufferedWriter(new OutputStreamWriter(socket
                                                      .getOutputStream()));
        receiveLock = new ReentrantLock();
        receivedAck = receiveLock.newCondition();
        this.running = true;
        ackReceived = ACKTYPES.NO_ACK;
        (new Thread(this)).start();
    }

    private boolean send(String message)
        throws IOException {
        if (ChatClientControl.logger != null) {
            ChatClientControl.logger.info("Send message: " + message);
        }
        bw.write(message + EOL);
        bw.flush();
        return receive();
    }

    private boolean receive()
        throws IOException {
        boolean retValue = true;

        receiveLock.lock();
        try {
            ackReceived = ACKTYPES.ACK_WAITING;
            receivedAck.await();
            retValue = ackReceived == ACKTYPES.ACK_RECEIVED;
            if (ChatClientControl.logger != null) {
                ChatClientControl.logger.info("ackReceived: " +
                                              ackReceived.name());
            }
        } catch (InterruptedException ie) {
            ackReceived = ACKTYPES.NACK_RECEIVED;
        } finally {
            receiveLock.unlock();
        }

        return retValue;
    }

    private void receiveAnyAck(boolean ack) {
        receiveLock.lock();
        try {
            ackReceived = ack ? ACKTYPES.ACK_RECEIVED : ACKTYPES.NACK_RECEIVED;
            receivedAck.signal();
        } finally {
            receiveLock.unlock();
        }
    }

    private void processMsg(String msg) {
        int spaceIdx = msg.indexOf(' ');
        String msgDes[] = new String[2];

        msgDes[0] = msg.substring(0, spaceIdx);
        msgDes[1] = msg.substring(spaceIdx + 1);

        switch(msgDes[0].toUpperCase()) {
        case "CONNECT":
            chatClient.setRemoteConnected();
            break;

        case "STATUS":
            chatClient.setRemoteStatus(msgDes[1]);
            break;

        case "MESSAGE":
            chatClient.receiveRemoteMsg(msgDes[1]);
            break;

        case "DISCONNECT":
            chatClient.setRemoteDisconnected();
            break;

        case "ACK":
            receiveAnyAck(true);
            break;

        case "NACK":
            receiveAnyAck(false);
            break;
        }
    }

    public boolean connect(String name)
        throws IOException {
        return send(CONNECT + name);
    }

    public boolean status(String status)
        throws IOException {
        return send(STATUS + status);
    }

    public boolean sendMessage(String msg)
        throws IOException {
        return send(MESSAGE + msg);
    }

    public boolean disconnect() throws IOException {
        boolean retValue = send(DISCONNECT);
        // if (retValue) {
        //     if (running)
        //         running = false;
        // }
        if (running) {
            // bf.close();
            // bw.close();
            // socket.close();
            running = false;
        }
        return retValue;
    }

    @Override
    public void run() {
        while (running) {
            try {
                String msg = bf.readLine();
                if (msg == null) {
                    running = false;
                    continue;
                }
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
