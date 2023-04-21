package com.epam.rd.chat.client;

import java.util.List;
import java.io.IOException;

interface IChatClient extends ChatClientService {
    List<String> getMessage(ChatUser user, int from, int to);
    int getNumberMessages(ChatUser user);
    void setRemoteConnected();
    void setRemoteStatus(String st);
    void receiveRemoteMsg(String msg);
    void setRemoteDisconnected();
}
