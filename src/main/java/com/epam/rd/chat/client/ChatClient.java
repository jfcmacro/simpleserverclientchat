package com.epam.rd.chat.client;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient implements IChatClient,
                                   ObservableChatEvent {
    private List<List<String>> messages;
    private String status[];
    private StubChatClient stub;
    private boolean partyConnected;
    private List<ChatObserver> observers;

    public ChatClient(String host, int port)
        throws UnknownHostException, IOException {
        this.stub = new StubChatClient(this, host, port);
        this.messages = new Vector<>(ChatUser.values().length);
        this.observers = new ArrayList<>();

        for (ChatUser cu : ChatUser.values())
            messages.add(cu.ordinal(), new ArrayList<>());

        this.status = new String[ChatUser.values().length];

        this.partyConnected = false;
    }

    public boolean connect(String user)
        throws IOException {
        return stub.connect(user);
    }

    public boolean sendMessage(String msg)
        throws IOException {
        if (!partyConnected) return false;

        boolean retValue = stub.sendMessage(msg);

        if (retValue) {
            messages.get(ChatUser.LOCAL.ordinal()).add(msg);
            ChatEvent.SENDMSG_CHATEVENT.setData(msg);
            notifyObservers(ChatEvent.SENDMSG_CHATEVENT);
        }

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

    public boolean disconnect()
        throws IOException {
        return stub.disconnect();
    }

    public List<String> getMessage(ChatUser user, int from, int to) {
        if (from > to)
            throw new IllegalArgumentException("from value great than to");
        if (from < 0 || to < 0)
            throw new IllegalArgumentException("Argument with negative value");

        return messages.get(user.ordinal()).subList(from, to);
    }

    public int getNumberMessages(ChatUser user) {
        return messages.get(user.ordinal()).size();
    }

    public String getStatus(ChatUser user) {
        return status[user.ordinal()];
    }

    public void setRemoteConnected() {
        partyConnected = true;
        notifyObservers(ChatEvent.CONNECT_CHATEVENT);
    }

    public void setRemoteStatus(String st) {
        status[ChatUser.REMOTE.ordinal()] = st;
    }

    public void receiveRemoteMsg(String msg) {
        messages.get(ChatUser.REMOTE.ordinal()).add(msg);
        ChatEvent.RECEIVEMSG_CHATEVENT.setData(msg);
        notifyObservers(ChatEvent.RECEIVEMSG_CHATEVENT);
    }

    public void setRemoteDisconnected() {
        partyConnected = false;
        notifyObservers(ChatEvent.DISCONNECT_CHATEVENT);
    }

    public void addObserver(ChatObserver o) {
        observers.add(o);
    }

    public void removeObserver(ChatObserver o) {
        observers.remove(o);
    }

    public void notifyObservers(ChatEvent event) {
        observers.stream()
            .forEach((o) -> o.handleEvent(event));
    }
}
