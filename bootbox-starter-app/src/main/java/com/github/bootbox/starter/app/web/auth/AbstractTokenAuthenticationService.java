package com.github.bootbox.starter.app.web.auth;

import com.alibaba.fastjson.JSON;
import com.github.bootbox.redis.RedisService;
import com.github.bootbox.security.token.TokenAuthenticationService;
import com.github.bootbox.security.token.TokenInformation;
import com.github.bootbox.starter.app.redis.RedisKeys;
import com.github.bootbox.starter.app.web.api.AppRequest;
import com.github.bootbox.starter.app.web.gateway.token.TokenInfo;
import com.github.bootbox.starter.app.web.gateway.token.UserToken;
import com.github.bootbox.web.api.ApiHttpRequest;
import com.github.bootbox.web.api.ApiRequestHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public abstract class AbstractTokenAuthenticationService implements TokenAuthenticationService {
    private final RedisService redisProvider;

    public AbstractTokenAuthenticationService(RedisService redisProvider) {
        this.redisProvider = redisProvider;
    }

    @Override
    public TokenInformation authenticateToken(HttpServletRequest httpServletRequest) {
        final ApiHttpRequest currentApiRequest = ApiRequestHolder.getCurrentApiRequest();
        final AppRequest apiRequest = (AppRequest) currentApiRequest.getApiRequest();
        final Optional<TokenInfo> tokenOpt = findToken(apiRequest.getUid());
        final String token = apiRequest.getToken();
        final String uid = apiRequest.getUid() + "";
        if (tokenOpt.isPresent()) {
            final TokenInfo tokenInfo = tokenOpt.get();
            boolean valid = true;
            if (token == null || !token.equals(tokenInfo.getToken())) {
                valid = false;
            }
            if (tokenInfo.getExpire() < System.currentTimeMillis()) {
                valid = false;
            }
            final TokenInformation tokenInformation = new TokenInformation(uid,
                    token, valid);
            return tokenInformation;
        } else {
            final TokenInformation tokenInformation = new TokenInformation(uid,
                    token, false);
            return tokenInformation;
        }
    }

    private Optional<TokenInfo> findToken(final Integer uid) {
        String tokenInfoStr = redisProvider.hget(RedisKeys.USER_REQ_TOKEN, uid + "");
        if (tokenInfoStr != null && tokenInfoStr instanceof String) {
            return Optional.of(JSON.parseObject(tokenInfoStr, TokenInfo.class));
        }
        UserToken user = findUserToken(uid);
        if (user == null) {
            return Optional.empty();
        }
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(user.getToken());
        tokenInfo.setExpire(user.getExpire().getTime());
        return Optional.of(tokenInfo);
    }

    protected abstract UserToken findUserToken(Integer uid);
}
