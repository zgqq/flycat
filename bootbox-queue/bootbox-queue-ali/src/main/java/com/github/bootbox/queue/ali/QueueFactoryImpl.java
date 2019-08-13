/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
