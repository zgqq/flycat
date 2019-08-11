package com.github.bootbox.starter.app.web.gateway.token;

import java.util.Date;

public class UserToken {
    private Date expire;
    private String token;

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
