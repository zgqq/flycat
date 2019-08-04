package com.github.bootbox.wechat.mp;

import com.alibaba.fastjson.JSON;
import com.github.bootbox.util.http.HttpUtils;
import com.github.bootbox.util.http.Pair;
import org.apache.commons.lang3.StringUtils;
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
            LOGGER.info("Requested wechat mp auth info, response:{}", JSON.toJSONString(pair));
            if (null != pair && 200 == pair.getL().intValue()) {
                map = JSON.parseObject(pair.getR(), Map.class);
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
