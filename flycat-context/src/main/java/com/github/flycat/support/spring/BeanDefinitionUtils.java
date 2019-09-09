package com.github.flycat.support.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.function.Supplier;

public class BeanDefinitionUtils {

    public static <T> GenericBeanDefinition register(BeanDefinitionRegistry registry,
                                                     Class<T> clazz,
                                                     String name,
                                                     Supplier<T> supplier) {
        final GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(clazz);
        genericBeanDefinition.setInstanceSupplier(supplier);
        registry.registerBeanDefinition(name, genericBeanDefinition);
        return genericBeanDefinition;
    }
}
