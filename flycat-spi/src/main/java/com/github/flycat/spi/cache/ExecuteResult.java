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
package com.github.flycat.spi.cache;

public class ExecuteResult<T> {
    private final boolean executed;
    private final T returnValue;

    public ExecuteResult(boolean executed, T returnValue) {
        this.executed = executed;
        this.returnValue = returnValue;
    }

    public boolean isExecuted() {
        return executed;
    }

    public T getReturnValue() {
        return returnValue;
    }
}
