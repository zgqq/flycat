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
package com.github.flycat.spi;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.context.ContextUtils;
import com.github.flycat.context.util.ConfigurationUtils;

public interface SpiService {

    default Integer getInteger(String key) {
        try {
            return ConfigurationUtils.getInteger(getApplicationConfiguration(), key);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get key, "+key, e);
        }
    }

    default String getString(String key) {
        return ConfigurationUtils.getString(getApplicationConfiguration(), key);
    }

    default ApplicationConfiguration getApplicationConfiguration() {
        return ContextUtils.getApplicationConfiguration();
    }
}
