package com.github.bootbox.starter.app.web;

import com.github.bootbox.starter.app.web.api.Result;
import com.github.bootbox.web.api.ApiFactory;

public class BootboxApiFactoryAdapter implements ApiFactory {

    @Override
    public Object createApiResult(int code, String message) {
        return new Result<>(code, message);
    }
}
