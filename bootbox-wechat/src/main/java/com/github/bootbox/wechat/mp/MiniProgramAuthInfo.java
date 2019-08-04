package com.github.bootbox.wechat.mp;

import java.util.StringJoiner;

public class MiniProgramAuthInfo {
    private String accessToken;
    private String openid;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MiniProgramAuthInfo.class.getSimpleName() + "[", "]")
                .add("accessToken='" + accessToken + "'")
                .add("openid='" + openid + "'")
                .toString();
    }
}
