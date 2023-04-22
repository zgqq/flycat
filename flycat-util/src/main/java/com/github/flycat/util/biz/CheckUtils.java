package com.github.flycat.util.biz;

public class CheckUtils {
    public static boolean normalRange(long... args) {
        for (long arg : args) {
            if (arg < 0 || arg > 10000 * 100) {
                return false;
            }
        }
        return true;
    }


    public static boolean normalRange(int... args) {
        for (int arg : args) {
            if (arg < 0 || arg > 10000 * 100) {
                return false;
            }
        }
        return true;
    }
}
