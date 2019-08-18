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
package com.github.flycat.spi.impl.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.flycat.spi.SpiService;
import com.github.flycat.spi.config.ConfigException;
import com.github.flycat.spi.config.ConfigService;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.concurrent.Callable;

@Singleton
public class NacosConfigService implements ConfigService, SpiService {
    private com.alibaba.nacos.api.config.ConfigService configService;

    public NacosConfigService() {
    }

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

    @PostConstruct
    public void createConfigService() {
        final String addr = getString("flycat.nacos.config.server-addr");
        configService = NacosUtils.createConfigService(addr);
    }
}
