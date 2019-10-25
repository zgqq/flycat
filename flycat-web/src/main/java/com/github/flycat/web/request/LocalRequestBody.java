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
package com.github.flycat.web.request;

public final class LocalRequestBody {
    private static final ThreadLocal<RequestBodyHolder> REQUEST_HOLDER = new ThreadLocal<>();

    private LocalRequestBody() {
    }

    public static void setCurrentApiRequest(RequestBodyHolder apiHttpRequest) {
        REQUEST_HOLDER.set(apiHttpRequest);
    }

    public static RequestBodyHolder getCurrentRequest() {
        return REQUEST_HOLDER.get();
    }
}
