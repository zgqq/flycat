package com.github.bootbox.web.exception;

public class BaseException extends RuntimeException {


    // 错误编码
    private int errorCode = 0;
    // 内部错误编码
    private String internalCode = "";
    // 默认错误信息
    protected String defaultMessage = null;

    private String[] args = null;

    public BaseException() {
        super();
    }

    public BaseException(Throwable cause) {
        super(cause);
        if (cause instanceof BaseException) {
            BaseException e = (BaseException) cause;
            this.defaultMessage = e.defaultMessage;
            this.errorCode = e.errorCode;
        }
    }

    /**
     * @param cause     原始的异常信息
     * @param errorCode 错误编码
     */
    public BaseException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * @param cause          原始的异常信息
     * @param errorCode      错误编码
     * @param defaultMessage 默认显示信息，配置文件无对应值时显示
     */
    public BaseException(Throwable cause, int errorCode, String defaultMessage) {
        this(cause, errorCode);
        this.defaultMessage = defaultMessage;
    }

    /**
     * @param errorCode      错误编码
     * @param defaultMessage 默认显示信息，配置文件无对应值时显示
     */
    public BaseException(int errorCode, String defaultMessage) {
        super();
        this.errorCode = errorCode;
        this.defaultMessage = defaultMessage;
    }

    /**
     * @param errorCode      错误码
     * @param internalCode   内部错误编码
     * @param defaultMessage 显示信息
     */
    public BaseException(int errorCode, String internalCode, String defaultMessage) {
        super();
        this.errorCode = errorCode;
        this.defaultMessage = defaultMessage;
        this.internalCode = internalCode;
    }

    public BaseException(int errorCode, String[] args) {
        super();
        this.errorCode = errorCode;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return this.defaultMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

}
