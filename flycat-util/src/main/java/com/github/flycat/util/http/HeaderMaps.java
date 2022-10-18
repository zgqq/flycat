package com.github.flycat.util.http;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class HeaderMaps {

    public static Map<String, String> contentTypeUrlencoded() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        return map;
    }
}
