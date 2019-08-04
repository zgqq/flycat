package com.github.bootbox.autoconfigure;

import com.github.bootbox.container.ApplicationContainer;
import com.github.bootbox.container.ContainerUtils;
import com.github.bootbox.container.spring.SpringContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(ApplicationContainer.class)
public class SpringContextConfiguration {

    @Bean
    public ApplicationContainer containerHolder(@Autowired ApplicationContext webApplicationContext) {
        SpringContainer springContainer = new SpringContainer(webApplicationContext);
        ContainerUtils.setContainerHolder(springContainer);
        return springContainer;
    }
}
