package com.github.bootbox.web.api;

public class ApiFactoryHolder {
    private static volatile ApiFactory apiFactory;

    public static ApiFactory getApiFactory() {
        return apiFactory;
    }

    public static void setApiFactory(ApiFactory apiFactory) {
        ApiFactoryHolder.apiFactory = apiFactory;
    }
}
