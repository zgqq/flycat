package com.github.flycat.util;

public class NumberUtils {

    public static Integer toInteger(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number) {
            Number number = (Number) o;
            return number.intValue();
        }
        if (o instanceof String) {
            return org.apache.commons.lang3.math.NumberUtils.toInt((String) o);
        }
        return null;
    }
}
