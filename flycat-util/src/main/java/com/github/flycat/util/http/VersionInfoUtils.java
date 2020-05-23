/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.flycat.util.http;

public class VersionInfoUtils {
    
    private static final String USER_AGENT_PREFIX = "xiaoai-server";
    
    private static String version = null;
    
    private static String defaultUserAgent = null;

    public static String getVersion() {
        if (version == null) {
            version = "1.0";
        }
        return version;
    }
    
    public static String getDefaultUserAgent() {
        if (defaultUserAgent == null) {
            defaultUserAgent = USER_AGENT_PREFIX + "/" + getVersion() + "(" + 
                System.getProperty("os.name") + "/" + System.getProperty("os.version") + "/" +
                System.getProperty("os.arch") + ";" + System.getProperty("java.version") + ")";
        }
        return defaultUserAgent;
    }

}
