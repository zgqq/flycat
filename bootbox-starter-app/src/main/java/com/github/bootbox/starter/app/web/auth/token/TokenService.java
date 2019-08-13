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
package com.github.bootbox.starter.app.web.auth.token;

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
