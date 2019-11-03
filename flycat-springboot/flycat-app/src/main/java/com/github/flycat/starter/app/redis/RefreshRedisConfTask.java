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
package com.github.flycat.starter.app.redis;

import com.github.flycat.context.ApplicationContext;
import com.github.flycat.spi.json.JsonService;
import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.spi.task.TaskService;
import com.github.flycat.starter.app.config.AppConf;
import com.github.flycat.starter.app.config.MaintainConf;
import com.github.flycat.util.StringUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RefreshRedisConfTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshRedisConfTask.class);

    private final RedisService redisService;
    private final ApplicationContext applicationContainer;
    private final JsonService jsonService;
    private final TaskService taskService;

    public RefreshRedisConfTask(ApplicationContext applicationContainer,
                                RedisService redisService,
                                JsonService jsonService, TaskService taskService) {
        this.redisService = redisService;
        this.applicationContainer = applicationContainer;
        this.jsonService = jsonService;
        this.taskService = taskService;
    }

    public void start() {
        if (!redisService.isAvailable()) {
            LOGGER.info("Not found redis provider");
        }

        LOGGER.info("" +
                "Starting refresh redis conf task");
        taskService.addFixedDelayTaskInSecond(() -> {
            LOGGER.info("Refreshing redis conf");
            Map<String, String> stringStringMap = redisService.hGetAll(RedisConfKeys.CONF_RESPONSE_FILTER);
            int contentsSize = 0;
            if (stringStringMap != null) {
                Set<Map.Entry<String, String>> entries = stringStringMap.entrySet();
                Map<String, List<String>> replaceContentMap = new HashMap<>();
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (JsonUtils.isJsonArray(value)) {
                        final List<String> contents = jsonService.toStringList(value);
                        replaceContentMap.put(key, contents);
                    } else {
                        replaceContentMap.put(key, Lists.newArrayList(value));
                    }
                }

                if (entries != null) {
                    contentsSize = entries.size();
                }
                AppConf.setContentFilterMap(replaceContentMap);

            }


            final String applicationName = applicationContainer.getApplicationName();
            final String key = RedisConfKeys.CONF_SYSTEM_MAINTAIN + ":" + applicationName;
            LOGGER.info("Getting app maintain config, key:{}", key);
            String appMaintainConfig = redisService.get(key);

            if (StringUtils.isBlank(appMaintainConfig)) {
                appMaintainConfig = redisService.get(RedisConfKeys.CONF_SYSTEM_MAINTAIN);
            }

            MaintainConf maintainConfig = null;
            if (jsonService.isValidJson(appMaintainConfig)) {
                maintainConfig = jsonService.parseObject(appMaintainConfig,
                        MaintainConf.class);
            }
            AppConf.setMaintainConfig(maintainConfig);

            Set<String> smembers = redisService.sMembers(RedisConfKeys.CONF_DEBUG_UIDS);
            if (smembers == null) {
                smembers = new HashSet<>();
            }
            AppConf.setDebugUids(smembers);

            LOGGER.info("Refreshed filterContent contents, contentsSize:{}," +
                            " maintainConf:{}, debugUids:{}", contentsSize,
                    maintainConfig, smembers
            );
        }, 0, 20);
        LOGGER.info("Started refresh redis conf task");
    }
}
