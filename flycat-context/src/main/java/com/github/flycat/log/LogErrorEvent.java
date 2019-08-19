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
package com.github.flycat.log;

import org.slf4j.Logger;

public class LogErrorEvent {
    private final Logger logger;
    private final String message;
    private final Throwable throwable;

    public LogErrorEvent(Logger logger, String message, Throwable throwable) {
        this.logger = logger;
        this.message = message;
        this.throwable = throwable;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getMessage() {
        return message;
    }


    public Throwable getThrowable() {
        return throwable;
    }
}
