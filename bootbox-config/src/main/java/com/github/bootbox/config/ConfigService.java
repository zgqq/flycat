package com.github.bootbox.config;

public interface ConfigService {

    String getConfig(String dataId) throws ConfigException;

    String getConfig(String dataId, long timeoutMs) throws ConfigException;

    String getConfig(String dataId, String group, long timeoutMs) throws ConfigException;

    <T> T getJsonConfig(String dataId, Class<T> type) throws ConfigException;

    <T> T getJsonConfig(String dataId, String name, Class<T> type) throws ConfigException;
}
