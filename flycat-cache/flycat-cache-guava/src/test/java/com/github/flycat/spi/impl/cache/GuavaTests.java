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
