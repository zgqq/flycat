package com.github.bootbox.wechat;

public interface WechatLoginHandler {
    // return null if pass
    Object handle(WechatApiRequest apiRequest);
}
