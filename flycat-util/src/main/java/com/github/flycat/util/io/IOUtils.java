/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
