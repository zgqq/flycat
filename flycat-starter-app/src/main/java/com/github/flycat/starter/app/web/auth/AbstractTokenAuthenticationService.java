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
package com.github.flycat.starter.app.web.auth;

import com.alibaba.fastjson.JSON;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.security.token.TokenAuthenticationService;
import com.github.flycat.security.token.TokenInformation;
import com.github.flycat.starter.app.redis.RedisKeys;
import com.github.flycat.starter.app.web.api.AppRequest;
import com.github.flycat.starter.app.web.auth.token.TokenInfo;
import com.github.flycat.web.api.ApiHttpRequest;
import com.github.flycat.web.api.ApiRequestHolder;

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
        if (apiRequest == null) {
            final TokenInformation tokenInformation = new TokenInformation(null,
                    null, false);
            return tokenInformation;
        }
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
        TokenInfo user = findTokenInfo(uid);
        if (user == null) {
            return Optional.empty();
        }
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(user.getToken());
        tokenInfo.setExpire(user.getExpire());
        return Optional.of(tokenInfo);
    }

    protected abstract TokenInfo findTokenInfo(Integer uid);
}
