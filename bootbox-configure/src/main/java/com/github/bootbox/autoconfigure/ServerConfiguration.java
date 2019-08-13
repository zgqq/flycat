/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bootbox.autoconfigure;

import com.github.bootbox.server.event.EventManager;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Configuration
public class ServerConfiguration {

    @Bean
    public EventBusPostProcessor eventBusPostProcessor() {
        return new EventBusPostProcessor();
    }

    public static class EventBusPostProcessor implements BeanPostProcessor {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName)
                throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName)
                throws BeansException {
            // for each method in the bean
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                // check the annotations on that method
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    // if it contains the Subscribe annotation
                    if (annotation.annotationType().equals(Subscribe.class)) {
                        // register it with the event bus
                        EventManager.register(bean);
                        log.trace("Bean {} containing method {} was subscribed to {}",
                                new Object[]{
                                        beanName, method.getName(),
                                        EventBus.class.getCanonicalName()
                                });
                        // we only need to register once
                        return bean;
                    }
                }
            }
            return bean;
        }
    }
}
