package com.github.bootbox.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public final class CommonUtils {

    public static String getUUIDWithoutHyphen() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String md5(String text) {
        return DigestUtils.md5Hex(text);
    }
}
