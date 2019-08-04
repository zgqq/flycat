package com.github.bootbox.wechat;

import com.github.bootbox.web.api.ApiRequest;

public interface WechatApiRequest extends ApiRequest  {
    String getOpenid();

    String getToken();
}
