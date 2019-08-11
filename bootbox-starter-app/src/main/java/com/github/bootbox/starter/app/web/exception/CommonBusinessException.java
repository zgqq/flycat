package com.github.bootbox.starter.app.web.exception;

import com.github.bootbox.starter.app.web.api.ResultCode;

/**
 * Created by zgq
 * Date: 2018-07-25
 * Time: 6:30 PM
 *
 * @author zgq
 */
public class CommonBusinessException extends BusinessException {

    public CommonBusinessException(String defaultMessage) {
        this(ResultCode.CLIENT_UNKNOWN_ERROR, defaultMessage);
    }

    public CommonBusinessException(int errorCode) {
        super(errorCode);
    }

    public CommonBusinessException(int errorCode, String defaultMessage) {
        super(errorCode, defaultMessage);
    }

    public CommonBusinessException(int errorCode, String internalCode, String defaultMessage) {
        super(errorCode, internalCode, defaultMessage);
    }

    public CommonBusinessException(Throwable cause, int errorCode, String defaultMessage) {
        super(cause, errorCode, defaultMessage);
    }

    public CommonBusinessException(Throwable cause, int errorCode) {
        super(cause, errorCode);
    }

    public CommonBusinessException(int errorCode, String[] args) {
        super(errorCode, args);
    }
}
