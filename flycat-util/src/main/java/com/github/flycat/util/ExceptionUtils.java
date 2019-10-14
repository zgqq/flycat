package com.github.flycat.util;

public class ExceptionUtils {

    public static String getStackTrace(Throwable throwable) {
        return org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(throwable);
    }
}
