package com.github.flycat.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static Matcher matcher( String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher;
    }
}
