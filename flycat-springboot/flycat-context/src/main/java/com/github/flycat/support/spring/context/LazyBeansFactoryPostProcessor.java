package com.github.flycat.support.spring.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class LazyBeansFactoryPostProcessor implements BeanFactoryPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LazyBeansFactoryPostProcessor.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory ) throws BeansException {
        for ( String name : beanFactory.getBeanDefinitionNames() ) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
            if (beanDefinition.getBeanClassName() == null) {
                LOGGER.debug("Bean class name is null, {}", name);
                continue;
            }
            if ( beanDefinition.getBeanClassName().contains(".spi.")) {
                beanDefinition.setLazyInit(false);
                LOGGER.info("Checked spi bean {}, make it not lazy", beanDefinition.getBeanClassName());
            }
        }
    }
}