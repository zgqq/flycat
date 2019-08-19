/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.starter.app.config;

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
