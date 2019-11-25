package com.github.flycat.platform.springboot;

import com.github.flycat.util.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

public class PropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String tomcatEnableLog = "server.tomcat.accesslog.enabled";
        String tomcatLogDirKey = "server.tomcat.accesslog.directory";

        PropertySource<?> propertySource = new MapPropertySource("flycat-default",
                new DefaultPropertyHandler(environment)
                        .apply(tomcatEnableLog, true)
                        .applyOther(tomcatLogDirKey, "logging.file.path")
                        .getProperties()
        );
        environment.getPropertySources().addLast(propertySource);
    }


    static class DefaultPropertyHandler {
        private final ConfigurableEnvironment configurableEnvironment;
        private final Map<String, Object> properties = new HashMap<>();

        DefaultPropertyHandler(ConfigurableEnvironment configurableEnvironment) {
            this.configurableEnvironment = configurableEnvironment;
        }

        private Object getDefaultProperty(ConfigurableEnvironment environment,
                                          String key, Object defaultValue) {
            String value = environment.getProperty(key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
            return defaultValue;
        }

        public DefaultPropertyHandler applyOther(String key, String otherKey) {
            String value = configurableEnvironment.getProperty(key);
            if (StringUtils.isBlank(value)) {
                value = configurableEnvironment.getProperty(otherKey);
            }
            properties.put(key, value);
            return this;
        }

        public DefaultPropertyHandler apply(String key, Object defaultValue) {
            Object defaultProperty = getDefaultProperty(configurableEnvironment, key, defaultValue);
            properties.put(key, defaultProperty);
            return this;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }
    }
}