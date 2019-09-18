package com.github.flycat.spi.impl.redis;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.spi.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class SpringMultipleRedisService extends MultipleRedisService {

    @Inject
    public SpringMultipleRedisService(PrimaryRedisService primaryRedisService,
                                      SecondaryRedisService secondaryRedisService,
                                      ApplicationConfiguration applicationConfiguration) {
        super(primaryRedisService, secondaryRedisService, applicationConfiguration);
    }

    @Override
    public RedisService newRedisService(String host, Integer port, String password) {
        final StringRedisTemplate secondaryRedisTemplate =
                createRedisTemplate(host, port, password);
        return new SpringRedisProviderAdapter(secondaryRedisTemplate);
    }

    public StringRedisTemplate createRedisTemplate(String host, Integer port, String password) {
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
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }
}
