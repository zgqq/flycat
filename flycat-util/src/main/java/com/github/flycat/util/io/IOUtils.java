package com.github.flycat.util.io;

import java.io.*;
import java.nio.charset.Charset;

public class IOUtils {

    public static void read(InputStream inputStream, byte[] buffer) throws IOException {
        org.apache.commons.io.IOUtils.read(inputStream, buffer);
    }

    public static String getFileContentByPathname(String pathname) throws IOException {
        final File file = new File(pathname);
        final FileInputStream fileInputStream = new FileInputStream(file);
        return org.apache.commons.io.IOUtils.toString(fileInputStream, Charset.defaultCharset());
    }

    public static String getFileContentByClasspath(String pathname) throws IOException {
        final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathname);
        return org.apache.commons.io.IOUtils.toString(resourceAsStream, Charset.defaultCharset());
    }
}
