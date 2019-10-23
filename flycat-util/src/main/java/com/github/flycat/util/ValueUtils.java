package com.github.flycat.util;

public class ValueUtils {

    public static int integerToInt(Integer integer, int defaultInt) {
        return integer == null ? defaultInt : integer.intValue();
    }


    public static Integer defaultIfNull(Integer integer, Integer defaultValue) {
        return integer == null ? defaultValue : integer;
    }
}
