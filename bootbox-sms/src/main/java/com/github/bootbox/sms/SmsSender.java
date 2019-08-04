package com.github.bootbox.sms;

public interface SmsSender {

    void send(String phone, String msg);

    default void send(String phone, String msg, String ext) {
        throw new UnsupportedOperationException();
    }
}
