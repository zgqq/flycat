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
package com.github.flycat.spi.impl.config;

//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.flycat.spi.SpiService;
import com.github.flycat.spi.config.ConfigException;
import com.github.flycat.spi.config.ConfigService;
import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.spi.json.JsonObject;
import com.github.flycat.spi.json.JsonUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.Callable;

@Singleton
@Named
public class NacosConfigService implements ConfigService, SpiService {
    private com.alibaba.nacos.api.config.ConfigService configService;
    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    public NacosConfigService(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        createConfigService();
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public NacosConfigService(com.alibaba.nacos.api.config.ConfigService configService,
                              ApplicationConfiguration applicationConfiguration) {
        this.configService = configService;
        this.applicationConfiguration = applicationConfiguration;
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
        return checkException(() -> JsonUtils.parseObject(config, type));
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
            JsonObject jsonObject = JsonUtils.parseObject(config);
            return jsonObject.getJsonObject(name, type);
//            final JSONObject jsonObject = JSON.parseObject(config);
//            return jsonObject.getObject(name, type);
        });
    }

    @Override
    public JsonObject getJsonConfig(String dataId) throws ConfigException {
        final String config = getConfig(dataId);
        return JsonUtils.parseObject(config);
    }

    public void createConfigService() {
        final String addr = getString("flycat.nacos.config.server-addr");
        final String user = getString("flycat.nacos.config.user");
        final String password = getString("flycat.nacos.config.password");
        configService = NacosUtils.createConfigService(addr, user, password);
    }
}
