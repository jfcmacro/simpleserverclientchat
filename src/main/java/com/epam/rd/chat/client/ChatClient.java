package com.epam.rd.chat.client;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    private List<String> messages[];
    private String status[];
    private StubChatClient stub;
    private boolean partyConnected;

    public ChatClient(String host, int port)
        throws UnknownHostException, IOException {
        this.stub = new StubChatClient(this, host, port);

        this.messages = new List[ChatUser.values().length];
        for (ChatUser cu : ChatUser.values()) {
            messages[cu.ordinal()] = new ArrayList<>();
        }

        this.status = new String[ChatUser.values().length];

        this.partyConnected = false;
    }

    public boolean connect(String user) throws IOException {
        return stub.connect(user);
    }

    public boolean message(String msg)
        throws IOException {
        if (!partyConnected) return false;

        boolean retValue = stub.sendMessage(msg);

        if (retValue)
            messages[ChatUser.LOCAL.ordinal()].add(msg);

        return retValue;
    }

    public boolean status(String st)
        throws IOException {
        if (!partyConnected) return false;

        boolean retValue = stub.status(st);

        if (retValue)
            status[ChatUser.LOCAL.ordinal()] = st;

        return retValue;
    }

    public boolean disconnect() throws IOException {
        return stub.disconnect();
    }

    public List<String> getMessage(ChatUser user, int from, int to) {
        if (from > to)
            throw new IllegalArgumentException("from value great than to");
        if (from < 0 || to < 0)
            throw new IllegalArgumentException("Argument with negative value");

        return messages[user.ordinal()].subList(from, to);
    }

    public int numberMessages(ChatUser user) {
        return messages[user.ordinal()].size();
    }

    public String getStatus(ChatUser user) {
        return status[user.ordinal()];
    }

    public void setRemoteConnected() {
        partyConnected = true;
    }

    public void setRemoteStatus(String st) {
        status[ChatUser.REMOTE.ordinal()] = st;
    }

    public void receiveRemoteMsg(String msg) {
        messages[ChatUser.REMOTE.ordinal()].add(msg);
    }

    public void setRemoteDisconnected() {
        partyConnected = false;
    }
}
