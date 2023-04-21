package com.epam.rd.chat.client;

import java.io.IOException;

interface ChatClientService {
    boolean connect(String name) throws IOException;
    boolean status(String status) throws IOException;
    boolean disconnect() throws IOException;
    boolean sendMessage(String msg) throws IOException;
}
