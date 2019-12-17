package com.github.flycat.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class DateFormatter {

    private ThreadLocal<SimpleDateFormat> formatThreadLocal = new ThreadLocal<SimpleDateFormat>();
    private final String format;
    private static final ConcurrentHashMap<String, DateFormatter> formatterInstanceMap = new ConcurrentHashMap<>();
    public static final DateFormatter YYYY_MM_DD_HH_MM_SS_SSS = DateFormatter.getInstance("yyyy-MM-dd HH:mm:ss.SSS");
    public static final DateFormatter YYYY_MM_DD_HH_MM_SS = DateFormatter.getInstance("yyyy-MM-dd HH:mm:ss");
    public static final DateFormatter YYYYMMDD_HHMMSS = DateFormatter.getInstance("yyyyMMdd_HHmmss");

    public DateFormatter() {
        this("yyyy-MM-dd HH:mm:ss");
    }

    public static DateFormatter getInstance(String format) {
        DateFormatter newFormatter = new DateFormatter(format);
        DateFormatter dateFormatter = formatterInstanceMap.putIfAbsent(format, newFormatter);
        if (dateFormatter == null) {
            return newFormatter;
        }
        return dateFormatter;
    }

    public DateFormatter(String format) {
        this.format = format;
    }

    private SimpleDateFormat getDateFormat() {
        SimpleDateFormat dateFormat = formatThreadLocal.get();
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat();
            formatThreadLocal.set(dateFormat);
        }
        return dateFormat;
    }

    public String format(Date date) {
        return getDateFormat().format(date);
    }

    public Date parse(String dateStr) throws ParseException {
        return getDateFormat().parse(dateStr);
    }
}
