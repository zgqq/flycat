package com.github.flycat.context;

import com.github.flycat.module.Module;
import com.github.flycat.module.ModuleManager;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.properties.ServerEnvUtils;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ContextManager {

    public static void beforeRun(Class<? extends Module>... modules) {
        if (!ContextUtils.isLocalProfile()) {
            final String logDir = ServerEnvUtils.getProperty("logging.path");
            if (StringUtils.isNotBlank(logDir)) {
                try {
                    System.setOut(new PrintStream(new BufferedOutputStream(
                            new FileOutputStream(logDir + "console.out.log")),
                            true));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Unable to console file ", e);
                }
            }
        }
        ModuleManager.load(modules);
    }
}
