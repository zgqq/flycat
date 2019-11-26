package com.github.flycat.util.io;

import com.github.flycat.util.date.DateFormatter;

import java.io.OutputStream;
import java.util.Date;

public class FormatPrintStream extends java.io.PrintStream {

    private static DateFormatter dateFormatter = DateFormatter.getInstance(DateFormatter.YYYMMDD_HHMMSS_SSS);

    public FormatPrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void println(String string) {
        Date date = new Date();
        super.println("[" + dateFormatter.format(date) + "] " + string);
    }
}