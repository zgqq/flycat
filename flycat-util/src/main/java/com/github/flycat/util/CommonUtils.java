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
package com.github.flycat.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public final class CommonUtils {

    public static String getUUIDWithoutHyphen() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String md5(String text) {
        return DigestUtils.md5Hex(text);
    }
}
