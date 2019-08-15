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
package com.github.flycat.queue.ali;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.github.flycat.queue.AlwaysRestartHandler;
import com.github.flycat.queue.MessageHandler;
import com.github.flycat.queue.Queue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zgq
 * Date: 2017-10-23
 * Time: 11:03 AM
 */
public class QueueMessageHandler extends AlwaysRestartHandler implements Queue {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueMessageHandler.class);
    private final MessageHandler messageHandler;
    private String subName;
    private String queueName;
    private String topicName;
    private MNSClient client;
    private String tag;

    protected QueueMessageHandler(CloudAccountHolder cloudAccountHolder,
                                  String topicName,
                                  String tag, String module, String env,
                                  MessageHandler messageHandler) {
        this.topicName = topicName;
        this.queueName = module + "-queue";
        this.subName = module + "-sub";
        if (StringUtils.isNotBlank(env)) {
            this.queueName += "-" + env;
            this.subName += "-" + env;
        }
        this.tag = tag;
        this.client = cloudAccountHolder.getAccount().getMNSClient();
        this.messageHandler = messageHandler;
        init();
    }

    @Override
    public void start() {
        LOGGER.info("Starting listen queue, name:{}", queueName);
        super.start();
        LOGGER.info("Started  queue");
    }

    public void init() {
        LOGGER.info("Starting " + queueName + " queue handler... topicName {}", topicName);
        MNSUtils.createQueue(client, queueName, 30);
        //创建属性订阅
        MNSUtils.createSucscriptionWithQueueWithMeta(client, topicName, subName, queueName,
                tag, null, null);
        LOGGER.info("Finished " + queueName + " queue");
    }


    public void run() {
        try {
            CloudQueue queue = client.getQueueRef(queueName);
            while (!stop.get()) {
                List<Message> messages1 = queue.batchPopMessage(10, 5);
                if (messages1 != null && !messages1.isEmpty()) {
                    for (Message message : messages1) {
                        try {
                            if (message == null) {
                                continue;
                            }
                            if (message.getDequeueCount() < 3) {
                                messageHandler.handleMessage(message.getMessageBodyAsRawString());
                            }
                            queue.deleteMessage(message.getReceiptHandle());
                        } catch (Exception e) {
                            LOGGER.error("handleOne exception: "
                                    + message.getMessageId() + ", " + message.getMessageBodyAsRawString(), e);
                        }
                    }
                }
            }
        } catch (ClientException ce) {
            LOGGER.error("Something wrong with the network connection between client "
                    + "and MNS handler. Please check your network and DNS availablity.", ce);
        } catch (ServiceException se) {
            String m = "";
            if (se.getErrorCode().equals("QueueNotExist")) {
                m = "Queue is not exist.Please create queue before use";
            } else if (se.getErrorCode().equals("TimeExpired")) {
                m = "The request is time expired. Please check your local machine timeclock";
            }
            LOGGER.error(m, se);
        } catch (Exception e) {
            LOGGER.error("Unknown exception happened!", e);
        }
    }
}
