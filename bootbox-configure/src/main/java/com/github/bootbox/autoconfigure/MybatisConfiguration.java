package com.github.bootbox.autoconfigure;

import com.github.bootbox.db.mybatis.DataSourceConfig;
import com.github.bootbox.db.mybatis.DataSourceUtils;
import com.github.bootbox.db.mybatis.HikariConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * Created by zgq on 17-3-9.
 */
@Configuration
@ConditionalOnClass(MapperScannerConfigurer.class)
public class MybatisConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisConfiguration.class);
    public static final String SQL_SESSION_FACTORY_NAME_1 = "sqlSessionFactory1";


    @Configuration
    @AutoConfigureAfter(CreatePrimaryDataSourceConfiguration.class)
    @ConditionalOnBean(CreatePrimaryDataSourceConfiguration.class)
    @ConditionalOnClass(MapperScannerConfigurer.class)
    public static class Mybatis1Configuration {

        // http://www.importnew.com/25940.html
        @Bean
        public MapperScannerConfigurer createMapperScanner(
                ApplicationContext applicationContext) {
            String name = applicationContext.getEnvironment().
                    resolvePlaceholders("${ds.primary.mybatis.mapper}");
            LOGGER.info("Creating primary mybatis mapper, {}", name);
            return DataSourceUtils.createMapperConfigurer(name, SQL_SESSION_FACTORY_NAME_1);
        }


        @Bean(name = SQL_SESSION_FACTORY_NAME_1)
        public SqlSessionFactory createSqlSessionFactory(
                @Qualifier("primaryDataSource")
                        DataSource dataSource) throws Exception {
            return DataSourceUtils.createSessionFactory(dataSource);
        }
    }


    @Configuration
    @ConditionalOnBean(CreateSecondaryDataSourceConfiguration.class)
    @AutoConfigureAfter(CreateSecondaryDataSourceConfiguration.class)
    @ConditionalOnClass(MapperScannerConfigurer.class)
    public static class MybatisSqlFactory2Configuration {

        public static final String SQL_SESSION_FACTORY_NAME_2 = "sqlSessionFactory2";

        // http://www.importnew.com/25940.html
        @Bean
        public MapperScannerConfigurer createMapperScanner2(
                ApplicationContext applicationContext) {
            String name = applicationContext.getEnvironment().
                    resolvePlaceholders("${ds.secondary.mybatis.mapper}");
            LOGGER.info("Creating secondary mybatis mapper, {}", name);
            return DataSourceUtils.createMapperConfigurer(name, SQL_SESSION_FACTORY_NAME_2);
        }

        @Bean(name = SQL_SESSION_FACTORY_NAME_2)
        public SqlSessionFactory createSqlSessionFactory(
                @Qualifier("secondaryDataSource") DataSource dataSource
        ) throws Exception {
            return DataSourceUtils.createSessionFactory(dataSource);
        }
    }

    // https://github.com/spring-projects/spring-boot/issues/7815 @ConditionalOnBean
    @Configuration
    @AutoConfigureAfter(PrimaryDataSourceConfiguration.class)
    @ConditionalOnBean(PrimaryDataSourceConfiguration.class)
    public static class CreatePrimaryDataSourceConfiguration {

        @Bean(name = "primaryDataSource")
        @Primary
        public DataSource primaryDataSource(@Autowired @Qualifier("primaryDataSourceConfig")
                                                    DataSourceConfig dataSourceConfig,
                                            @Autowired @Qualifier("primaryHikariConfig")
                                                    HikariConfiguration hikariConfiguration) {
            LOGGER.info("Creating primary datasource, dataSource:{}, hikari:{}",
                    dataSourceConfig, hikariConfiguration);
            hikariConfiguration.setInitSQL(dataSourceConfig.getInitSQL());
            HikariDataSource dataSource = DataSourceUtils
                    .createDataSource(dataSourceConfig.getUrl(),
                            dataSourceConfig.getUsername(), dataSourceConfig.getPassword(),
                            dataSourceConfig.getDriverClassName(),
                            hikariConfiguration);
            return dataSource;
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
        public DataSource secondaryDataSource(@Qualifier("secondaryDataSourceConfig")
                                                      DataSourceConfig dataSourceConfig,
                                              @Qualifier("secondaryHikariConfig")
                                                      HikariConfiguration hikariConfiguration) {
            hikariConfiguration.setInitSQL(dataSourceConfig.getInitSQL());
            HikariDataSource dataSource = DataSourceUtils
                    .createDataSource(dataSourceConfig.getUrl(),
                            dataSourceConfig.getUsername(), dataSourceConfig.getPassword(),
                            dataSourceConfig.getDriverClassName(),
                            hikariConfiguration);
            return dataSource;
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
    @ConditionalOnProperty(name = "ds.primary.enable", havingValue = "true")
    public static class PrimaryDataSourceConfiguration {


        @Bean(name = "primaryDataSourceConfig")
        @ConfigurationProperties(prefix = "ds.primary")
        @Primary
        public DataSourceConfig dataSourceConfig() {
            LOGGER.info("Creating primary config");
            return new DataSourceConfig();
        }

        @Bean(name = "primaryHikariConfig")
        @ConfigurationProperties(prefix = "ds.primary.hikari")
        @Primary
        public HikariConfiguration configuration() {
            LOGGER.info("Creating hikari config");
            return new HikariConfiguration();
        }

    }

    @Configuration
    @ConditionalOnProperty(name = "ds.secondary.enable", havingValue = "true")
    public static class SecondaryDataSourceConfiguration {


        @Bean(name = "secondaryDataSourceConfig")
        @ConfigurationProperties(prefix = "ds.secondary")
        public DataSourceConfig dataSourceConfig() {
            return new DataSourceConfig();
        }

        @Bean(name = "secondaryHikariConfig")
        @ConfigurationProperties(prefix = "ds.secondary.hikari")
        public HikariConfiguration configuration() {
            return new HikariConfiguration();
        }

    }
}
