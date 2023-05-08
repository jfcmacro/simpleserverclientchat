package com.epam.rd.chat.server;

import java.io.IOException;

public class ChatService {

    private String stubsName[];
    private StubChatService stubs[];
    private int nConnected;
    private boolean echo;

    public ChatService(boolean echo) {
        this.nConnected = 0;
        this.stubs = new StubChatService[2];
        this.stubsName = new String[2];
        this.echo = echo;
    }

    private int indexOtherStub(StubChatService stub) {
        int i = 0;

        for (i = 0; i < stubs.length; i++)
            if (stubs[i].getSocketString().equals(stub.getSocketString()))
                break;

        return (i + 1) % 2;
    }

    public boolean connect(String name,
                           StubChatService stub)
        throws IOException {

        if (nConnected == stubs.length) return false;

        if (echo) {
            stubs[nConnected] = stub;
            stubsName[nConnected++] = name;
            stubs[nConnected] = stub;
            stubsName[nConnected++] = name;
        }
        else {
            stubs[nConnected] = stub;
            stubsName[nConnected++] = name;
        }

        if (nConnected == stubs.length) {
            if (echo) 
                stubs[indexOtherStub(stub)].connect(name);
            else {
                for (int i = 0; i < stubs.length; i++) {
                    stubs[i].connect(stubsName[(i + 1) % 2]);
                }
            }
        }

        return true;
    }

    public void status(String st,
                       StubChatService stub)
        throws IOException  {

        if (nConnected == stubs.length)
            stubs[indexOtherStub(stub)].status(st);
    }

    public void sendMessage(String msg,
                            StubChatService stub)
        throws IOException {

        if (nConnected == stubs.length)
            stubs[indexOtherStub(stub)].sendMessage(msg);
    }

    public void disconnect(StubChatService stub)
        throws IOException {
        if (nConnected == stubs.length) {
            stubs[indexOtherStub(stub)].disconnect();
            for (int i = 0; i < stubs.length; i++)
                stubs[i] = null;
            nConnected = 0;
        }
        else
            stubs[0] = null;
    }
}
