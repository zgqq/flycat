/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.exception;

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
