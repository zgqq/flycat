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
package com.github.flycat.spi.redis;

import java.util.Map;
import java.util.Set;

public interface RedisService {

    String hget(String redisKey, String key);

    Set<String> smembers(String key);

    String get(String key);

    Map<String, String> hgetAll(String key);

    void hsetAsJson(String key, String hashKey, Object object);

    void setex(String key, String value, long seconds);

    <T> T getJsonObject(String key, Class<T> clazz);

    void setexAsJson(String key, Object object, long seconds);
}
