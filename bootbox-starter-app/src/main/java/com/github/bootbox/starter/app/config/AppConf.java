package com.github.bootbox.starter.app.config;

import java.util.*;

public class AppConf {
    private static volatile Map<String, List<String>> contentFilterMap = new HashMap<>();
    private static volatile MaintainConf maintainConfig;
    private static volatile String debugKey;
    private static volatile Set<String> debugUids = new HashSet<>();

    public static Map<String, List<String>> getContentFilterMap() {
        return contentFilterMap;
    }

    public static void setContentFilterMap(Map<String, List<String>> contentFilterMap) {
        AppConf.contentFilterMap = contentFilterMap;
    }

    public static MaintainConf getMaintainConfig() {
        return maintainConfig;
    }

    public static void setMaintainConfig(MaintainConf maintainConfig) {
        AppConf.maintainConfig = maintainConfig;
    }

    public static String getDebugKey() {
        return debugKey;
    }

    public static void setDebugKey(String debugKey) {
        AppConf.debugKey = debugKey;
    }

    public static boolean isResponseMaintaining() {
        return AppConf.maintainConfig != null && "1".equals(AppConf.maintainConfig.getResponseSwitch() + "");
    }

    public static Set<String> getDebugUids() {
        return debugUids;
    }

    public static void setDebugUids(Set<String> debugUids) {
        AppConf.debugUids = debugUids;
    }
}
