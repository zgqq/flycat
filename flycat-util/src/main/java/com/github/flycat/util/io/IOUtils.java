package com.github.flycat.util.io;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    public static void read(InputStream inputStream, byte[] buffer) throws IOException {
        org.apache.commons.io.IOUtils.read(inputStream, buffer);
    }
}
