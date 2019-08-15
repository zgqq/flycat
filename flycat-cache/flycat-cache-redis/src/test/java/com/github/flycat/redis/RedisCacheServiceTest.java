package com.github.flycat.redis;


import com.alibaba.fastjson.JSON;
import org.junit.Test;

public class RedisCacheServiceTest {

    @Test
    public void testJson() {
        final String s = JSON.toJSONString(1);
        System.out.println(s);
        final Object jsonObject = JSON.parse(s);
        System.out.println(jsonObject);
    }
}
