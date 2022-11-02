package com.github.flycat.platform.springboot.context;


import com.github.flycat.context.ApplicationContext;
import com.github.flycat.context.RunContext;
import com.github.flycat.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringBootContextListener implements com.github.flycat.context.ContextListener {
    private static final Logger logger = LoggerFactory.getLogger(SpringBootContextListener.class);

    @Override
    public void beforeRun(RunContext runContext) {
        String property = System.getProperty("spring.profiles.active");
        if (StringUtils.isEmpty(property)) {
            logger.warn("Not set active profile, {}", property);
//            System.setProperty("spring.profiles.active", "local");
        }
    }

    @Override
    public void afterRun(ApplicationContext applicationContext) {

    }
}
