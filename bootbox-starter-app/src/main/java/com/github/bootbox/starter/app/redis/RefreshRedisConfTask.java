package com.github.bootbox.starter.app.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.bootbox.redis.RedisService;
import com.github.bootbox.container.ApplicationContainer;
import com.github.bootbox.starter.app.config.AppConf;
import com.github.bootbox.starter.app.config.MaintainConf;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RefreshRedisConfTask {

    private ScheduledExecutorService executorService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshRedisConfTask.class);

    private final RedisService redisClient;
    private final ApplicationContainer applicationContainer;

    public RefreshRedisConfTask(ApplicationContainer applicationContainer, RedisService redisClient) {
        this.redisClient = redisClient;
        this.applicationContainer = applicationContainer;
    }

    public void start() {
        LOGGER.info("" +
                "Starting refresh redis conf task");
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> {
            LOGGER.info("Refreshing redis conf");
            Map<String, String> stringStringMap = redisClient.hgetAll(RedisConfKeys.CONF_RESPONSE_FILTER);
            int contentsSize = 0;
            if (stringStringMap != null) {
                Set<Map.Entry<String, String>> entries = stringStringMap.entrySet();
                Map<String, List<String>> replaceContentMap = new HashMap<>();
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (JSON.isValidArray(value)) {
                        JSONArray jsonArray = JSON.parseArray(value);
                        List<String> contents = null;
                        if (jsonArray != null) {
                            contents = jsonArray.toJavaList(String.class);
                        } else {
                            contents = new ArrayList<>();
                        }
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
            String appMaintainConfig = redisClient.get(key);

            if (StringUtils.isBlank(appMaintainConfig)) {
                appMaintainConfig = redisClient.get(RedisConfKeys.CONF_SYSTEM_MAINTAIN);
            }

            MaintainConf maintainConfig = null;
            if (JSON.isValidObject(appMaintainConfig)) {
                maintainConfig = JSON.parseObject(appMaintainConfig,
                        MaintainConf.class);
            }
            AppConf.setMaintainConfig(maintainConfig);

            Set<String> smembers = redisClient.smembers(RedisConfKeys.CONF_DEBUG_UIDS);
            if (smembers == null) {
                smembers = new HashSet<>();
            }
            AppConf.setDebugUids(smembers);

            LOGGER.info("Refreshed filterContent contents, contentsSize:{}," +
                            " maintainConf:{}, debugUids:{}", contentsSize,
                    maintainConfig, smembers
            );
        }, 0, 20, TimeUnit.SECONDS);
        LOGGER.info("Started refresh redis conf task");
    }

    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
