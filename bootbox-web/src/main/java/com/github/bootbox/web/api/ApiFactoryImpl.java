package com.github.bootbox.web.api;

public class ApiFactoryImpl implements ApiFactory {
    @Override
    public Object createApiResult(int code, String message) {
        return new ApiResult(code, message);
    }
}
