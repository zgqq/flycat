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
package com.github.flycat.spi.impl.redis;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.util.StringUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Named
public class SpringMultipleRedisService extends MultipleRedisService
        implements InitializingBean, DisposableBean, BeanClassLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(SpringMultipleRedisService.class);

    private List<SpringRedisProviderAdapter> redisProviderAdapterList;

    @Inject
    public SpringMultipleRedisService(PrimaryRedisService primaryRedisService,
                                      SecondaryRedisService secondaryRedisService,
                                      ApplicationConfiguration applicationConfiguration) {
        super(primaryRedisService, secondaryRedisService, applicationConfiguration);
    }

    @Override
    public RedisService newRedisService(String host, Integer port, String password) {
        logger.info("Creating redis service, host:{}, port:{}, password:{}", host, port, password);
        final StringRedisTemplate secondaryRedisTemplate =
                createRedisTemplate(host, port, password);
        final SpringRedisProviderAdapter adapter = new SpringRedisProviderAdapter(secondaryRedisTemplate);
        if (redisProviderAdapterList == null) {
            redisProviderAdapterList = Lists.newArrayList();
        }
        redisProviderAdapterList.add(adapter);
        return adapter;
    }

    public StringRedisTemplate createRedisTemplate(String host, Integer port, String password) {
        if (StringUtils.isBlank(host)) {
            return null;
        }
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        if (StringUtils.isBlank(password)) {
            redisStandaloneConfiguration.setPassword(RedisPassword.none());
        } else {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setHostName(host);
        LettuceConnectionFactory redisConnectionFactory =
                new LettuceConnectionFactory(redisStandaloneConfiguration);

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
//        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        for (SpringRedisProviderAdapter adapter : redisProviderAdapterList) {
            adapter.setBeanClassLoader(classLoader);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (SpringRedisProviderAdapter adapter : redisProviderAdapterList) {
            adapter.afterPropertiesSet();
        }
    }

    @Override
    public void destroy() throws Exception {
        for (SpringRedisProviderAdapter adapter : redisProviderAdapterList) {
            adapter.destroy();
        }
    }
}
