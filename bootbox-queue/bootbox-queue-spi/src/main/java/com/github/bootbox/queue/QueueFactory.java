package com.github.bootbox.queue;

public interface QueueFactory {
    Queue createQueue(String tag, String module, MessageHandler messageHandler);
}
