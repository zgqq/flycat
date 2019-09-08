package com.github.flycat.support.spring.druid;

import com.alibaba.fastjson.JSON;
import com.github.flycat.platform.datasource.DruidStatProperties;
import com.github.flycat.spi.impl.context.SpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class SpringDruidLoader implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDruidLoader.class);

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        final SpringConfiguration springConfiguration = new SpringConfiguration(this.applicationContext);
        final DruidStatProperties druidStatProperties = springConfiguration.load("flycat.datasource.druid",
                DruidStatProperties.class);
        LOGGER.info("Loaded druid config, value:{}", JSON.toJSONString(druidStatProperties));
        if (druidStatProperties.getWebStatFilter().isEnabled()) {
            final GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
            genericBeanDefinition.setBeanClass(FilterRegistrationBean.class);
            genericBeanDefinition.setInstanceSupplier((Supplier<FilterRegistrationBean>) () ->
                    SpringDruidUtils.webStatFilterRegistrationBean(druidStatProperties));
            registry.registerBeanDefinition("webStatFilterRegistrationBean", genericBeanDefinition);
        }
        if (druidStatProperties.getStatViewServlet().isEnabled()) {
            final GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
            genericBeanDefinition.setBeanClass(ServletRegistrationBean.class);
            genericBeanDefinition.setInstanceSupplier((Supplier<ServletRegistrationBean>) () ->
                    SpringDruidUtils.statViewServletRegistrationBean(druidStatProperties));
            registry.registerBeanDefinition("statViewServletRegistrationBean", genericBeanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
