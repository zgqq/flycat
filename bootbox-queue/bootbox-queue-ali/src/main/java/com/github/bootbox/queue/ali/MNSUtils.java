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

import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.*;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by zcy on 17-5-25.
 */
public final class MNSUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MNSUtils.class);

    private MNSUtils() {
    }

    public static void createQueue(MNSClient client, String queueName, int pollingWaitSecond) {
        try {
            QueueMeta qMeta = new QueueMeta();
            qMeta.setQueueName(queueName);
            qMeta.setPollingWaitSeconds(pollingWaitSecond);
            //use long polling when queue is empty.
            CloudQueue cQueue = client.createQueue(qMeta);
            String url = cQueue.getQueueURL();
            LOGGER.info("Create queue successfully. URL: " + url);
        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service."
                    + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        } catch (ServiceException se) {
            if (se.getErrorCode().equals("QueueNotExist")) {
                LOGGER.info("Queue is not exist.Please create before use");
            } else if (se.getErrorCode().equals("TimeExpired")) {
                LOGGER.info("The request is time expired. Please check your local machine timeclock");
            }
            se.printStackTrace();
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
        }
    }

    public static void deleteQueue(MNSClient client, String queueName) {
        try {
            CloudQueue queue = client.getQueueRef(queueName);
            queue.delete();
            LOGGER.info("Delete cloud-queue-demo successfully!");
        } catch (ClientException ce) {
            LOGGER.info("Something wrong with the network connection between client and MNS service."
                    + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        } catch (ServiceException se) {
            if (se.getErrorCode().equals("QueueNotExist")) {
                LOGGER.info("Queue is not exist.Please create before use");
            } else if (se.getErrorCode().equals("TimeExpired")) {
                LOGGER.info("The request is time expired. Please check your local machine timeclock");
            }
            se.printStackTrace();
        } catch (Exception e) {
            LOGGER.info("Unknown exception happened!");
            e.printStackTrace();
        }
    }

    public static void createSubscriptionWithQueue(MNSClient mnsClient,
                                                   String topicName, String subName, String queueName) {
        CloudTopic topic = mnsClient.getTopicRef(topicName);
        String queueEndpoint = topic.generateQueueEndpoint(queueName);
        createSubscription(mnsClient, topicName, subName, queueEndpoint);
    }

    public static void createSubscription(MNSClient mnsClient, String topicName, String subName, String endPoint) {
        CloudTopic topic = mnsClient.getTopicRef(topicName);
        SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(subName);
        subMeta.setEndpoint(endPoint);
        subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.SIMPLIFIED);
        subMeta.setNotifyStrategy(SubscriptionMeta.NotifyStrategy.BACKOFF_RETRY);
        String subUrl = topic.subscribe(subMeta);
        LOGGER.info("subscription url: " + subUrl);
    }

    /**
     *
     */
    public static void createSucscriptionWithQueueWithMeta(MNSClient mnsClient, String topicName,
                                                           String subName, String queueName, String filterTag,
                                                           SubscriptionMeta.NotifyContentFormat notifyContentFormat,
                                                           SubscriptionMeta.NotifyStrategy notifyStrateg) {
        CloudTopic topic = mnsClient.getTopicRef(topicName);
        String queueEndpoint = topic.generateQueueEndpoint(queueName);
        createSubscription(mnsClient, topicName, subName, queueEndpoint, filterTag, notifyContentFormat, notifyStrateg);
    }

    public static void createSubscription(MNSClient mnsClient, String topicName,
                                          String subName, String endPoint, String filterTag,
                                          SubscriptionMeta.NotifyContentFormat notifyContentFormat,
                                          SubscriptionMeta.NotifyStrategy notifyStrateg) {
        CloudTopic topic = mnsClient.getTopicRef(topicName);
        SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName(subName);
        subMeta.setEndpoint(endPoint);
        subMeta.setNotifyContentFormat(notifyContentFormat == null ?
                SubscriptionMeta.NotifyContentFormat.SIMPLIFIED : notifyContentFormat);
        subMeta.setNotifyStrategy(notifyStrateg == null ?
                SubscriptionMeta.NotifyStrategy.BACKOFF_RETRY : notifyStrateg);
        if (filterTag != null && !filterTag.isEmpty()) {
            subMeta.setFilterTag(filterTag);
        }
        String subUrl = topic.subscribe(subMeta);
        LOGGER.info("subscription url: " + subUrl);
    }

    public static void deleteSubscription(MNSClient mnsClient, String topicName, String subName) {
        try {
            CloudTopic topic = mnsClient.getTopicRef(topicName);
            topic.unsubscribe(subName);
            LOGGER.info("unsubscribe successfully");
        } catch (Exception e) {
            LOGGER.info("unknown exception", e);
        }
    }

    public static List<Message> popQueueMsg(MNSClient mnsClient, String queueName) {
        try {
            CloudQueue queue = mnsClient.getQueueRef(queueName);
            List<Message> popMsg = queue.batchPopMessage(10);
            return popMsg;
        } catch (Exception e) {
            LOGGER.info("unknown exception", e);
        }
        return null;
    }


    public static Map<String, Object> getSubMetaInfo(MNSClient mnsClient, String topicName, String subName) {
        CloudTopic topic = mnsClient.getTopicRef(topicName);
        //获得订阅属性
        SubscriptionMeta sm = topic.getSubscriptionAttr(subName);
        Map<String, Object> meta = ImmutableMap.<String, Object>builder()
                .put("endPoint", sm.getEndpoint())
                .put("filterTag", sm.getFilterTag())
                .put("notifyContentFormat", sm.getNotifyContentFormat())
                .put("subUrl", sm.getSubscriptionURL())
                .put("notifyStrategy", sm.getNotifyStrategy())
                .build();
        return meta;
    }

    public static void setSubMeta(MNSClient mnsClient, String topicName, String subName,
                                  String endPoint, String filterTag,
                                  SubscriptionMeta.NotifyContentFormat notifyContentFormat,
                                  SubscriptionMeta.NotifyStrategy notifyStrategy, String subUrl) {
        CloudTopic topic = mnsClient.getTopicRef(topicName);
        SubscriptionMeta sm = topic.getSubscriptionAttr(subName);
        //设置订阅属性
        sm.setEndpoint(endPoint);
//        sm.setFilterTag(filterTag);
        sm.setNotifyContentFormat(notifyContentFormat);
        sm.setNotifyStrategy(notifyStrategy);
        sm.setSubscriptionURL(subUrl);
        topic.setSubscriptionAttr(sm);
    }

    public static void pushTopicMsg(MNSClient mnsClient, String topicName, String message) {
        CloudTopic topic = mnsClient.getTopicRef(topicName);
        try {
            TopicMessage msg = new RawTopicMessage(); //可以使用TopicMessage结构，选择不进行Base64加密
            msg.setMessageBody(message);
            msg = topic.publishMessage(msg);
            LOGGER.info(msg.getMessageId());
            LOGGER.info(msg.getMessageBodyMD5());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("subscribe error");
        }
    }

    public static void pushTopicMsgWithTag(MNSClient mnsClient, String topicName, String message, String tag) {
        CloudTopic topic = mnsClient.getTopicRef(topicName);
        try {
            TopicMessage msg = new RawTopicMessage(); //可以使用TopicMessage结构，选择不进行Base64加密
            msg.setMessageBody(message);
            msg.setMessageTag(tag);
            msg = topic.publishMessage(msg);
            LOGGER.info(msg.getMessageId());
            LOGGER.info(msg.getMessageBodyMD5());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("subscribe error");
        }
    }

    public static void pushQueueMsg(MNSClient mnsClient, String queueName, Object msgEntity) {
        CloudQueue queue = mnsClient.getQueueRef(queueName);
        pushQueueMsg(msgEntity, queue);
    }

    public static void pushDelayQueueMsg(MNSClient mnsClient, String queueName, Object msgEntity, int delaySeconds) {
        CloudQueue queue = mnsClient.getQueueRef(queueName);
        pushDelayQueueMsg(msgEntity, queue, delaySeconds);
    }

    public static void pushQueueMsg(Object msgEntity, CloudQueue queue) {
        try {
            Message message = new Message();
            message.setMessageBody(JSONObject.toJSONString(msgEntity), Message.MessageBodyType.RAW_STRING);
            Message m1 = queue.putMessage(message);
            LOGGER.info("queue msg:{}", m1.getMessageId());
        } catch (Exception e) {
            LOGGER.info("unknown exception", e);
        }
    }

    public static void pushDelayQueueMsg(Object msgEntity, CloudQueue queue, int delaySeconds) {
        try {
            Message message = new Message();
            message.setDelaySeconds(delaySeconds);
            message.setMessageBody(JSONObject.toJSONString(msgEntity), Message.MessageBodyType.RAW_STRING);
            Message m1 = queue.putMessage(message);
            LOGGER.info("queue msg:{}", m1.getMessageId());
        } catch (Exception e) {
            LOGGER.info("unknown exception", e);
        }
    }

    public static void main(String[] args) {

//        String queueName = MNSNames.TEST_LOGIN_RECORD_QUEUE;
//        String subName = MNSNames.TEST_LOGIN_RECORD_SUB;
//        String topicName = MNSNames.TEST_TOPIC;

//        String queueName = MNSNames.ONLINE_LOGIN_RECORD_QUEUE;
//        String subName = MNSNames.ONLINE_LOGIN_RECORD_SUB;
//        String topicName = MNSNames.ONLINE_TOPIC;
//
////        MNSClientHolder mnsClientHolder = new MNSClientHolder(accessKeyId, accessKeySecret, endPoint);
////        MNSClient client = mnsClientHolder.getInstance();
//
////        deleteQueue(client, queueName);
////        deleteSubscription(client, topicName, subName);
//        //创建队列
//        createQueue(client, queueName, 30);
////        createSubscriptionWithQueue(client, topicName, subName, queueName);
//        //创建属性订阅
//        createSucscriptionWithQueueWithMeta(client, topicName, subName, queueName, "login-event", null, null);
//
//        client.close();
    }

}
