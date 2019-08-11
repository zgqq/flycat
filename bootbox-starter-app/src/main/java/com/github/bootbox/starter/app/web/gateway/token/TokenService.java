package com.github.bootbox.starter.app.web.gateway.token;

import com.github.bootbox.redis.RedisService;
import com.github.bootbox.starter.app.redis.RedisKeys;
import com.github.bootbox.util.CommonUtils;
import com.github.bootbox.util.DateTimeUtils;
import org.jetbrains.annotations.NotNull;

public class TokenService {

    private final RedisService redisClient;

    public TokenService(RedisService redisClient) {
        this.redisClient = redisClient;
    }

    public void getAndSetToken(String uid) {
        final TokenInfo tokenInfo = generateTokenInfo();
        redisClient.hsetAsJson(RedisKeys.USER_REQ_TOKEN, uid + "", tokenInfo);
    }

    @NotNull
    private TokenInfo generateTokenInfo() {
        final TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setExpire(DateTimeUtils.currentTimeMillisPlusDays(30));
        tokenInfo.setToken(generateToken());
        tokenInfo.setActive(System.currentTimeMillis());
        return tokenInfo;
    }

    public String generateToken() {
        return CommonUtils.getUUIDWithoutHyphen();
    }
}
