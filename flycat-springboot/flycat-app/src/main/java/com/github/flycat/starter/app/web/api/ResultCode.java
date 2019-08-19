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
package com.github.flycat.starter.app.web.api;

// user friendly
public interface ResultCode {

    int OK = 0;

    // auth  100 - 199

    int CLIENT_UNKNOWN_ERROR = 100;

    int AUTH_TOKEN_EXPIRED = 102;

    // user 200 - 299
    int USER_UNKNOWN_ERROR = 200;

    // server
    int SERVER_UNKNOWN_ERROR = 500;


}
