package com.github.bootbox.queue.ali;

public class AliConnectConfig {
    private String endpoint;
    private String yourAccessKey;
    private String yourAccessId;
    private String topicName;
    private String env;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getYourAccessKey() {
        return yourAccessKey;
    }

    public void setYourAccessKey(String yourAccessKey) {
        this.yourAccessKey = yourAccessKey;
    }

    public String getYourAccessId() {
        return yourAccessId;
    }

    public void setYourAccessId(String yourAccessId) {
        this.yourAccessId = yourAccessId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }
}
