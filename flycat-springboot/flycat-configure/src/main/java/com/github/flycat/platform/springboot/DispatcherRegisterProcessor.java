package com.github.flycat.platform.springboot;

import com.github.flycat.dispatch.Dispatcher;
import com.github.flycat.dispatch.JavassistDispatcher;
import com.github.flycat.module.ModuleManager;
import com.google.common.collect.Lists;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.*;
import java.util.stream.Collectors;

public class DispatcherRegisterProcessor implements BeanDefinitionRegistryPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherRegisterProcessor.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final Reflections reflections = new Reflections(ModuleManager.getModulePackages(),
                contextClassLoader);
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Dispatcher.class, false);
        typesAnnotatedWith = typesAnnotatedWith.stream().filter(Class::isInterface).collect(Collectors.toSet());

        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(contextClassLoader));

        final HashMap<Class<?>, RootBeanDefinition> dispatchClass = new HashMap<>();
        for (Class<?> aClass : typesAnnotatedWith) {
            final Class aClass1 = JavassistDispatcher.generateClass(aClass);
            final RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(aClass1);
            dispatchClass.put(aClass, rootBeanDefinition);
        }

        final HashMap<Class<?>, List<String>> objectObjectHashMap = new HashMap<>();
        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
            final BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            final String beanClassName = beanDefinition.getBeanClassName();
            try {
                final Class<?> aClass = Class.forName(beanClassName);
                for (Class<?> aClass1 : typesAnnotatedWith) {
                    if (aClass1.isAssignableFrom(aClass)) {
                        LOGGER.info("Adding bean to list, name:{}, class:{}, dispatch:{}", beanDefinitionName, aClass.getName(),
                                aClass1.getName());
                        final List<String> beanDefinitions = objectObjectHashMap.get(aClass1);
                        if (beanDefinitions == null) {
                            objectObjectHashMap.put(aClass1, Lists.newArrayList(beanDefinitionName));
                        } else {
                            beanDefinitions.add(beanDefinitionName);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        for (Map.Entry<Class<?>, RootBeanDefinition> classRootBeanDefinitionEntry : dispatchClass.entrySet()) {
            final Class<?> key = classRootBeanDefinitionEntry.getKey();
            final RootBeanDefinition value = classRootBeanDefinitionEntry.getValue();
            List<String> beanDefinitions = objectObjectHashMap.get(key);
            if (beanDefinitions == null) {
                beanDefinitions = new ArrayList<>();
            }
            LOGGER.info("Handling dispatcher, class:{}, beanNames:{}", key.getName(), beanDefinitions);
            value.getPropertyValues().addPropertyValue("eventExecutorList",
                    beanDefinitions.stream().map(RuntimeBeanReference::new).collect(Collectors.toList())
                            .toArray(new RuntimeBeanReference[]{})
            );
            value.setPrimary(true);
            registry.registerBeanDefinition(key.getSimpleName(), value);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
