package com.epam.rd.chat.client;

interface ObservableChatEvent {
    void addObserver(ChatObserver o);
    void removeObserver(ChatObserver o);
    void notifyObservers(ChatEvent event, String data);
}
