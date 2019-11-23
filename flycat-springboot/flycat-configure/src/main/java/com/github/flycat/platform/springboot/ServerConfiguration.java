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
package com.github.flycat.platform.springboot;

import com.github.flycat.event.EventManager;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Configuration
public class ServerConfiguration {

    @Bean
    public static EventBusPostProcessor eventBusPostProcessor() {
        return new EventBusPostProcessor();
    }

    public static class EventBusPostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private Set<Object> listeners = Collections.synchronizedSet(new HashSet<>());
        private List<CompletableFuture<Void>> completableFutures = new ArrayList<>();

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName)
                throws BeansException {
            return checkAndAddListener(bean, beanName);
        }

        private Object checkAndAddListener(Object bean, String beanName) {
//            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                // for each method in the bean
                Method[] methods = bean.getClass().getMethods();
                for (Method method : methods) {
                    // check the annotations on that method
                    Annotation[] annotations = method.getAnnotations();
                    for (Annotation annotation : annotations) {
                        // if it contains the Subscribe annotation
                        if (annotation.annotationType().equals(Subscribe.class)) {
                            // register it with the event bus
//                        registerListener(bean, beanName, method);
                            infoListener(bean, beanName, method);
                            listeners.add(bean);
                        }
                    }
                }
//            });
//            completableFutures.add(completableFuture);
            return bean;
        }

        private void infoListener(Object bean, String beanName, Method method) {
            log.trace("Bean {} containing method {} was subscribed to {}",
                    new Object[]{
                            beanName, method.getName(),
                            EventBus.class.getCanonicalName()
                    });
            // we only need to register once
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[]{})).join();
            for (Object listener : listeners) {
                EventManager.register(listener);
            }
        }
    }
}
