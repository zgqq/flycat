package com.github.bootbox.config.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.bootbox.config.ConfigException;
import com.github.bootbox.config.ConfigService;

import java.util.concurrent.Callable;

public class NacosConfigService implements ConfigService {
    private final com.alibaba.nacos.api.config.ConfigService configService;

    public NacosConfigService(com.alibaba.nacos.api.config.ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public String getConfig(String dataId) throws ConfigException {
        return getConfig(dataId, Constants.DEFAULT_GROUP, 3000);
    }

    @Override
    public String getConfig(String dataId, long timeoutMs) throws ConfigException {
        return getConfig(dataId, Constants.DEFAULT_GROUP, timeoutMs);
    }

    @Override
    public String getConfig(String dataId, String group, long timeoutMs) throws ConfigException {
        try {
            return configService.getConfig(dataId, group,
                    timeoutMs);
        } catch (NacosException e) {
            throw new ConfigException("Unable to get config from nacos", e);
        }
    }

    @Override
    public <T> T getJsonConfig(String dataId, Class<T> type) throws ConfigException {
        final String config = getConfig(dataId);
        return checkException(() -> JSON.parseObject(config, type));
    }

    private <T> T checkException(Callable<T> callable) throws ConfigException {
        try {
            return callable.call();
        } catch (Throwable throwable) {
            throw new ConfigException(throwable);
        }
    }

    @Override
    public <T> T getJsonConfig(String dataId, String name, Class<T> type) throws ConfigException {
        final String config = getConfig(dataId);
        return checkException(() -> {
            final JSONObject jsonObject = JSON.parseObject(config);
            return jsonObject.getObject(name, type);
        });
    }
}
