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

import com.github.flycat.component.datasource.DataSourceConfig;
import com.github.flycat.component.datasource.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.config.TransactionManagementConfigUtils;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class);


    // https://github.com/spring-projects/spring-boot/issues/7815 @ConditionalOnBean
    @Configuration
    @AutoConfigureAfter(PrimaryDataSourceConfiguration.class)
    @ConditionalOnBean(PrimaryDataSourceConfiguration.class)
    public static class CreatePrimaryDataSourceConfiguration {

        @Bean(name = "primaryDataSource", destroyMethod = "close")
        @Primary
        public DataSource primaryDataSource(
                @Autowired DataSourceFactory dataSourceFactory,
                @Autowired @Qualifier("primaryDataSourceConfig")
                        DataSourceConfig dataSourceConfig) {
            LOGGER.info("Creating primary datasource, dataSource:{}",
                    dataSourceConfig);
            return dataSourceFactory.createDataSource(dataSourceConfig);
        }

        @Bean(name = "primaryTransaction")
        @Primary
        public DataSourceTransactionManager dataSourceTransactionManager(
                @Qualifier("primaryDataSource")
                        DataSource dataSource) {
            DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
            return dataSourceTransactionManager;
        }
    }


    @Configuration
    @AutoConfigureAfter(SecondaryDataSourceConfiguration.class)
    @ConditionalOnBean(SecondaryDataSourceConfiguration.class)
    public static class CreateSecondaryDataSourceConfiguration {

        @Bean(name = "secondaryDataSource", destroyMethod = "close")
        public DataSource secondaryDataSource(
                @Autowired DataSourceFactory dataSourceFactory,
                @Qualifier("secondaryDataSourceConfig")
                        DataSourceConfig dataSourceConfig) {
            return dataSourceFactory.createDataSource(dataSourceConfig);
        }

        @Bean(name = "secondaryTransaction")
        @ConditionalOnBean(name = "secondaryDataSource")
        public DataSourceTransactionManager dataSourceTransactionManager(
                @Qualifier("secondaryDataSource")
                        DataSource dataSource) {
            DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
            return dataSourceTransactionManager;
        }
    }


    @Configuration
    @ConditionalOnProperty(name = "flycat.datasource.primary.enabled", havingValue = "true")
    public static class PrimaryDataSourceConfiguration {

        @Bean(name = "primaryDataSourceConfig")
        @ConfigurationProperties(prefix = "flycat.datasource.primary")
        @Primary
        public DataSourceConfig dataSourceConfig() {
            LOGGER.info("Creating primary config");
            return new DataSourceConfig();
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "flycat.datasource.secondary.enabled", havingValue = "true")
    public static class SecondaryDataSourceConfiguration {
        @Bean(name = "secondaryDataSourceConfig")
        @ConfigurationProperties(prefix = "flycat.datasource.secondary")
        public DataSourceConfig dataSourceConfig() {
            return new DataSourceConfig();
        }
    }

    @Configuration
    public static class ProxyTransactionManagementConfiguration {

        @Bean(name = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor() {
            BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
            advisor.setTransactionAttributeSource(transactionAttributeSource());
            advisor.setAdvice(transactionInterceptor());
            return advisor;
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public TransactionAttributeSource transactionAttributeSource() {
            return new CustomTransactionAttributeSource();
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public TransactionInterceptor transactionInterceptor() {
            TransactionInterceptor interceptor = new TransactionInterceptor();
            interceptor.setTransactionAttributeSource(transactionAttributeSource());
            return interceptor;
        }
    }
}
