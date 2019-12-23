package com.github.flycat.util;

public class ArrayUtils {

    public static <T> T[] add(T[] array, T element) {
        return org.apache.commons.lang3.ArrayUtils.add(array, element);
    }
}
