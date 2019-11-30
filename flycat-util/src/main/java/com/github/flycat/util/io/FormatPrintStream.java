package com.github.flycat.util.io;

import com.github.flycat.util.date.DateFormatter;

import java.io.OutputStream;
import java.util.Date;

public class FormatPrintStream extends java.io.PrintStream {

    public FormatPrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void println(String string) {
        Date date = new Date();
        super.println("[" + DateFormatter.YYYY_MM_DD_HH_MM_SS_SSS.format(date) + "] " + string);
    }
}