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
package com.github.flycat.web.context;

public class ExceptionContext {
    private final Throwable throwable;
    private final boolean responseBodyHandler;

    public ExceptionContext(Throwable throwable, boolean responseBodyHandler) {
        this.throwable = throwable;
        this.responseBodyHandler = responseBodyHandler;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isResponseBodyHandler() {
        return responseBodyHandler;
    }
}
