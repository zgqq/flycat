package com.github.bootbox.queue.ali;


import com.github.bootbox.queue.MessageHandler;
import com.github.bootbox.queue.Queue;
import com.github.bootbox.queue.QueueFactory;

public class QueueFactoryImpl implements QueueFactory {
    private final CloudAccountHolder cloudAccountHolder;
    private final String topicName;
    private final String env;

    public QueueFactoryImpl(CloudAccountHolder cloudAccountHolder, String topicName, String env) {
        this.cloudAccountHolder = cloudAccountHolder;
        this.topicName = topicName;
        this.env = env;
    }

    @Override
    public Queue createQueue(String tag, String module, MessageHandler messageHandler) {
        return new QueueMessageHandler(cloudAccountHolder, topicName, tag,
                module, env, messageHandler);
    }
}
