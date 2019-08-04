package com.github.bootbox.web.api;

public class ApiResult {
    private final Integer code;
    private final String message;
    private Object data;

    public ApiResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
