package com.github.flycat.util;

public class ValueUtils {

    public static int integerToInt(Integer integer, int defaultInt) {
        return integer == null ? defaultInt : integer.intValue();
    }
}
