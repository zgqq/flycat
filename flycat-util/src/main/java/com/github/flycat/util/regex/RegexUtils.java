package com.github.flycat.util.regex;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static Matcher matcher( String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher;
    }


    public static String group(String value, String regex) {
        Matcher matcher = matcher(value, regex);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String group(String value, String regex, int group) {
        Matcher matcher = matcher(value, regex);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }


    public static Optional<Integer> extractInteger(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Optional.of(Integer.valueOf(matcher.group(1)));
        }
        return Optional.empty();
    }

    public static Optional<Long> extractLong(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Optional.of(Long.valueOf(matcher.group(1)));
        }
        return Optional.empty();
    }

}
