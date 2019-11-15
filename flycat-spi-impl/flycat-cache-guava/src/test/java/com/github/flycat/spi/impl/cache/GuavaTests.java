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
package com.github.flycat.spi.impl.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaTests {

    @Test
    public void testGetLoad() throws ExecutionException {
        Cache<Object, Object> objectCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .maximumSize(2048)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    final Object test = objectCache.get("test", new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            return "2";
                        }
                    });
                    assert test.equals("1");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        final Object test = objectCache.get("test", new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Thread.sleep(200L);
                return "1";
            }
        });
        assert "1".equals(test);
    }
}
