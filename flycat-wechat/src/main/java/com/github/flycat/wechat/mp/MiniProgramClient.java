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
package com.github.flycat.wechat.mp;

import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.http.HttpUtils;
import com.github.flycat.util.http.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MiniProgramClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiniProgramClient.class);
    private final String appid;
    private final String secret;

    public MiniProgramClient(String appid, String secret) {
        this.appid = appid;
        this.secret = secret;
    }

    public MiniProgramAuthInfo getAuthInfo(String code) {
        Map<String, String> params = new HashMap<>();

        //通过code换取网页授权access_token 和网页授权openid
        params.put("appid", appid);
        params.put("secret", secret);
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        Pair<Integer, String> pair = null;
        Map<String, String> map = null;

        try {
            LOGGER.info("Requesting wechat mp auth info, params {}", params);
            pair = HttpUtils.get("https://api.weixin.qq.com/sns/jscode2session", params, "utf-8");
            LOGGER.info("Requested wechat mp auth info, response:{}", JsonUtils.toJsonString(pair));
            if (null != pair && 200 == pair.getL().intValue()) {
                map = JsonUtils.parseObject(pair.getR(), Map.class);
            }

            if (StringUtils.isEmpty(map.get("openid"))) {
                LOGGER.error("Unable to get wechat mp auth info, code:{}, invalid response:{}",
                        code,
                        map);
                return null;
            }

            MiniProgramAuthInfo authInfo = new MiniProgramAuthInfo();
            authInfo.setAccessToken(map.get("access_token"));
            authInfo.setOpenid(map.get("openid"));
            return authInfo;
        } catch (Exception e) {
            throw new RuntimeException("Unable to get wechat mp auth info", e);
        }
    }
}
