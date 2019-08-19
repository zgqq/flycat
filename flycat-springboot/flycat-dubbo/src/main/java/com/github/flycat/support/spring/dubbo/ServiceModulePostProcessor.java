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
package com.github.flycat.support.spring.dubbo;

import com.github.flycat.module.ModuleManager;
import org.apache.dubbo.config.spring.beans.factory.annotation.ServiceAnnotationBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class ServiceModulePostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {

    private ClassLoader classLoader;
    private Environment environment;
    private ResourceLoader resourceLoader;
    private ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor;

    public ServiceModulePostProcessor() {
        final String[] packageNamesOfServiceModules = ModuleManager.getPackageNamesOfServiceModules();
        if (packageNamesOfServiceModules != null && packageNamesOfServiceModules.length > 0) {
            serviceAnnotationBeanPostProcessor = new ServiceAnnotationBeanPostProcessor(packageNamesOfServiceModules);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (serviceAnnotationBeanPostProcessor != null) {
            setAwareFields();
            serviceAnnotationBeanPostProcessor.postProcessBeanDefinitionRegistry(registry);
        }
    }

    private void setAwareFields() {
        serviceAnnotationBeanPostProcessor.setBeanClassLoader(classLoader);
        serviceAnnotationBeanPostProcessor.setEnvironment(environment);
        serviceAnnotationBeanPostProcessor.setResourceLoader(resourceLoader);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (serviceAnnotationBeanPostProcessor != null) {
            setAwareFields();
            serviceAnnotationBeanPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
