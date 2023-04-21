package com.epam.rd.chat.client;

interface ChatObserver {
    void handleEvent(ChatEvent event);
}
