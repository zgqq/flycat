package com.github.flycat.platform.springboot;

import com.github.flycat.module.Module;
import com.github.flycat.module.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

public class SpringBootPlatform {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootPlatform.class);

    public static void run(Class<?> primarySource, String[] args, Class<? extends Module>... modules) {
        try {
            ModuleManager.load(modules);
            SpringApplication.run(primarySource, args);
        } catch (Exception e) {
            LOGGER.error("Unable to startup server, system exit", e);
            System.exit(0);
        }
    }
}
