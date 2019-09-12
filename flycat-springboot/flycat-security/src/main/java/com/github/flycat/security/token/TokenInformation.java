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
package com.github.flycat.security.token;

public class TokenInformation {
    private final String userId;
    private final String token;
    private final boolean valid;

    public TokenInformation(String userId, String token, boolean valid) {
        this.userId = userId;
        this.token = token;
        this.valid = valid;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public boolean isValid() {
        return valid;
    }
}
