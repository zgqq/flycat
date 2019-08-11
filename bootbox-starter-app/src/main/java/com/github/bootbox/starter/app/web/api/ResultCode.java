package com.github.bootbox.starter.app.web.api;

// user friendly
public interface ResultCode {

    int OK = 0;

    // auth  100 - 199

    int CLIENT_UNKNOWN_ERROR = 100;

    int AUTH_TOKEN_EXPIRED = 102;

    // user 200 - 299
    int USER_UNKNOWN_ERROR = 200;

    // server
    int SERVER_UNKNOWN_ERROR = 500;


}
