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
