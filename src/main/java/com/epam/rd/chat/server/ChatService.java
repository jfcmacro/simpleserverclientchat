package com.epam.rd.chat.server;

public class ChatService {

    private StubChatService stub = null;

    public boolean connect(String name, StubChatService other) {
        if (stub != null) return false;
        stub = other;
        stub.connect(name);
        return true;
    }

    public void status(String status, StubChatService other) {
        if (stub != null)
            stub.status(status);
    }

    public void sendMessage(String msg, StubChatService other) {
        if (stub != null)
            stub.sendMessage(msg);
    }

    public void disconnect(StubChatService stub) {
        if (stub != null)
            stub.disconnect();
    }
}
