package com.epam.rd.chat.client;

enum ChatEvent {
    CONNECT_CHATEVENT,
    DISCONNECT_CHATEVENT,
    RECEIVEMSG_CHATEVENT,
    SENDMSG_CHATEVENT;
    private String data;
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
}
