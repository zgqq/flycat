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
package com.github.flycat.starter.app.web.auth;

import com.alibaba.fastjson.JSON;
import com.github.flycat.security.token.TokenAuthentication;
import com.github.flycat.security.token.TokenAuthenticationService;
import com.github.flycat.security.token.TokenInformation;
import com.github.flycat.spi.redis.RedisService;
import com.github.flycat.starter.app.redis.RedisKeys;
import com.github.flycat.starter.app.web.api.AppRequest;
import com.github.flycat.starter.app.web.auth.token.TokenInfo;
import com.github.flycat.web.api.ApiHttpRequest;
import com.github.flycat.web.api.ApiRequestHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public abstract class AbstractTokenAuthenticationService implements TokenAuthenticationService {
    private final RedisService redisProvider;

    public AbstractTokenAuthenticationService(RedisService redisProvider) {
        this.redisProvider = redisProvider;
    }

    @Override
    public TokenAuthentication requireToken(HttpServletRequest httpServletRequest) {
        final ApiHttpRequest currentApiRequest = ApiRequestHolder.getCurrentApiRequest();
        final AppRequest apiRequest = (AppRequest) currentApiRequest.getApiRequest();
        if (apiRequest == null) {
            final TokenAuthentication tokenInformation = new TokenAuthentication(null,
                    null);
            return tokenInformation;
        }
        final String token = apiRequest.getToken();
        final Integer uid = apiRequest.getUid();
        return new TokenAuthentication(uid, token);
    }

    @Override
    public TokenAuthentication authenticateToken(TokenAuthentication tokenAuthentication) {
        Integer uid = (Integer) tokenAuthentication.getPrincipal();
        String token = (String) tokenAuthentication.getCredentials();
        final TokenInfo tokenInfo = findToken((Integer) tokenAuthentication.getPrincipal());
        if (tokenInfo != null) {
            boolean valid = true;
            if (token == null || !token.equals(tokenInfo.getToken())) {
                valid = false;
            }
            if (tokenInfo.getExpire() < System.currentTimeMillis()) {
                valid = false;
            }

            final String authorities = tokenInfo.getAuthorities();
            List<GrantedAuthority> grantedAuthorities = null;
            if (StringUtils.isNotBlank(authorities)) {
                final String[] roles = authorities.split(",");
                grantedAuthorities = AuthorityUtils.createAuthorityList(roles);
            }
            final TokenAuthentication tokenInformation = new TokenAuthentication(uid,
                    token, grantedAuthorities, valid);
            return tokenInformation;
        } else {
            final TokenAuthentication tokenInformation = new TokenAuthentication(uid,
                    token, null, false);
            return tokenInformation;
        }
    }

    protected TokenInfo findToken(final Integer uid) {
        if (uid == null) {
            return null;
        }
        String tokenInfoStr = redisProvider.hget(RedisKeys.USER_REQ_TOKEN, uid + "");
        if (tokenInfoStr != null && tokenInfoStr instanceof String) {
            final TokenInfo tokenInfo = JSON.parseObject(tokenInfoStr, TokenInfo.class);
            return tokenInfo;
        }
        TokenInfo user = findTokenInfo(uid);
        if (user == null) {
            return null;
        }
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(user.getToken());
        tokenInfo.setExpire(user.getExpire());
        return null;
    }

    protected TokenInfo findTokenInfo(Integer uid) {
        return null;
    }
}
