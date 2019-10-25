package com.github.flycat.spi.cache;

import java.lang.reflect.Type;

public interface CacheOperation {

    default String createModuleNameByStackTrace(Type type) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final StackTraceElement stackTraceElement = stackTrace[3];
        final String className = stackTraceElement.getClassName();
        final String methodName = stackTraceElement.getMethodName();
        String flag = className + "." + methodName;
        if (type != null) {
            flag += "-" + type.getTypeName();
        }
        return flag;
    }

    default boolean removeCache(String module, String key) {
        throw new UnsupportedOperationException();
    }

    default boolean removeCache(String module) {
        throw new UnsupportedOperationException();
    }
}
