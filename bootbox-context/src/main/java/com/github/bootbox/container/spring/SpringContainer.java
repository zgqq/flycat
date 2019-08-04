package com.github.bootbox.container.spring;

import com.github.bootbox.container.ApplicationContainer;
import org.springframework.context.ApplicationContext;

public class SpringContainer implements ApplicationContainer {
    private final ApplicationContext applicationContext;

    public SpringContainer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    @Override
    public Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    @Override
    public String getApplicationName() {
        return this.applicationContext.getEnvironment().getProperty("spring.application.name");
    }
}
