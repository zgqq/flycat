package com.github.flycat.platform.springboot;

import com.github.flycat.platform.datasource.DataSourceConfig;
import com.github.flycat.platform.datasource.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class);


    // https://github.com/spring-projects/spring-boot/issues/7815 @ConditionalOnBean
    @Configuration
    @AutoConfigureAfter(PrimaryDataSourceConfiguration.class)
    @ConditionalOnBean(PrimaryDataSourceConfiguration.class)
    public static class CreatePrimaryDataSourceConfiguration {

        @Bean(name = "primaryDataSource")
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

        @Bean(name = "secondaryDataSource")
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
}
