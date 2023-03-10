package com.epam.rd.chat.server;

import java.io.IOException;

public class ChatService {

    private StubChatService stub = null;

    public boolean connect(String name,
                           StubChatService other) throws IOException {
        if (stub != null) return false;
        stub = other;
        stub.connect(name);
        return true;
    }

    public void status(String status,
                       StubChatService other) throws IOException  {
        if (stub != null)
            stub.status(status);
    }

    public void sendMessage(String msg,
                            StubChatService other) throws IOException {
        if (stub != null)
            stub.sendMessage(msg);
    }

    public void disconnect(StubChatService other) throws IOException {
        if (stub != null) {
            stub.disconnect();
            stub = null;
        }
    }
}
