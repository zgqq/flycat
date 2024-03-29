/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.spi.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisOperations {

    String hGet(String redisKey, String key);

    Set<String> sMembers(String key);

    String get(String key);

    Map<String, String> hGetAll(String key);

    void setEx(String key, long seconds, String value);

    Long del(String... key);

    Boolean zAdd(String key, double score, String member);

    Set<String> zRange(String key, int start, int end);

    Boolean setNx(String key, String value);

    Boolean expire(String key, int seconds);

    void multi();

    List<Object> exec();

    Long incr(String key);

    Long incr(String key, long delta);

    boolean exists(String key);

    Boolean setNxAndExpire(String key, String value, Integer expireSeconds);
}
