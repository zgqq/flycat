package com.github.flycat.platform.springboot.context;


import com.github.flycat.context.ApplicationContext;
import com.github.flycat.context.RunContext;

public class SpringBootContextListener implements com.github.flycat.context.ContextListener {

    @Override
    public void beforeRun(RunContext runContext) {
        String property = System.getProperty("spring.profiles.active");
        if (property == null) {
            System.setProperty("spring.profiles.active", "dev");
        }
    }

    @Override
    public void afterRun(ApplicationContext applicationContext) {

    }
}
