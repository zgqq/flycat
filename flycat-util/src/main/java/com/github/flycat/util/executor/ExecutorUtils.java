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
package com.github.flycat.util.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {

    public static ThreadPoolExecutor newExecutor(int core, String factoryName) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(factoryName + "-%d")
                .setDaemon(true)
                .build();
        return new ThreadPoolExecutor(core, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }
}
