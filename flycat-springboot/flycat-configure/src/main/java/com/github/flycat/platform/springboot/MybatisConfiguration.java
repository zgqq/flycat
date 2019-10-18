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

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.flycat.db.mybatis.MybatisUtils;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

import static com.github.flycat.module.ModuleManager.getModulePackagesAsString;


/**
 *
 */
@Configuration
//@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
public class MybatisConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisConfiguration.class);
    public static final String SQL_SESSION_FACTORY_NAME_1 = "sqlSessionFactory1";


    public static class MybatisPlusAutoConfiguration {

        private static final Logger logger = LoggerFactory.getLogger(MybatisPlusAutoConfiguration.class);

        private final MybatisPlusProperties properties;

        private final Interceptor[] interceptors;

        private final ResourceLoader resourceLoader;

        private final DatabaseIdProvider databaseIdProvider;

        private final List<ConfigurationCustomizer> configurationCustomizers;

        private final List<MybatisPlusPropertiesCustomizer> mybatisPlusPropertiesCustomizers;

        private final ApplicationContext applicationContext;


        public MybatisPlusAutoConfiguration(MybatisPlusProperties properties,
                                            ObjectProvider<Interceptor[]> interceptorsProvider,
                                            ResourceLoader resourceLoader,
                                            ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                            ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                            ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider,
                                            ApplicationContext applicationContext) {
            this.properties = properties;
            this.interceptors = interceptorsProvider.getIfAvailable();
            this.resourceLoader = resourceLoader;
            this.databaseIdProvider = databaseIdProvider.getIfAvailable();
            this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
            this.mybatisPlusPropertiesCustomizers = mybatisPlusPropertiesCustomizerProvider.getIfAvailable();
            this.applicationContext = applicationContext;
        }

        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        @ConditionalOnMissingBean
        public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
            // TODO 使用 MybatisSqlSessionFactoryBean 而不是 SqlSessionFactoryBean
            MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
            factory.setDataSource(dataSource);
            factory.setVfs(SpringBootVFS.class);
            if (StringUtils.hasText(this.properties.getConfigLocation())) {
                factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
            }
            applyConfiguration(factory);
            if (this.properties.getConfigurationProperties() != null) {
                factory.setConfigurationProperties(this.properties.getConfigurationProperties());
            }
            if (!ObjectUtils.isEmpty(this.interceptors)) {
                factory.setPlugins(this.interceptors);
            }
            if (this.databaseIdProvider != null) {
                factory.setDatabaseIdProvider(this.databaseIdProvider);
            }
            if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
                factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
            }
            if (this.properties.getTypeAliasesSuperType() != null) {
                factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
            }
            if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
                factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
            }
            if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
                factory.setMapperLocations(this.properties.resolveMapperLocations());
            }

            // TODO 自定义枚举包
            if (StringUtils.hasLength(this.properties.getTypeEnumsPackage())) {
                factory.setTypeEnumsPackage(this.properties.getTypeEnumsPackage());
            }
            // TODO 此处必为非 NULL
            GlobalConfig globalConfig = this.properties.getGlobalConfig();
            // TODO 注入填充器
            if (this.applicationContext.getBeanNamesForType(MetaObjectHandler.class,
                    false, false).length > 0) {
                MetaObjectHandler metaObjectHandler = this.applicationContext.getBean(MetaObjectHandler.class);
                globalConfig.setMetaObjectHandler(metaObjectHandler);
            }
            // TODO 注入主键生成器
            if (this.applicationContext.getBeanNamesForType(IKeyGenerator.class, false,
                    false).length > 0) {
                IKeyGenerator keyGenerator = this.applicationContext.getBean(IKeyGenerator.class);
                globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
            }
            // TODO 注入sql注入器
            if (this.applicationContext.getBeanNamesForType(ISqlInjector.class, false,
                    false).length > 0) {
                ISqlInjector iSqlInjector = this.applicationContext.getBean(ISqlInjector.class);
                globalConfig.setSqlInjector(iSqlInjector);
            }
            // TODO 设置 GlobalConfig 到 MybatisSqlSessionFactoryBean
            factory.setGlobalConfig(globalConfig);
            return factory.getObject();
        }

        // TODO 入参使用 MybatisSqlSessionFactoryBean
        private void applyConfiguration(MybatisSqlSessionFactoryBean factory) {
            // TODO 使用 MybatisConfiguration
            com.baomidou.mybatisplus.core.MybatisConfiguration configuration = this.properties.getConfiguration();
            if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
                configuration = new com.baomidou.mybatisplus.core.MybatisConfiguration();
            }
            if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
                for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                    customizer.customize(configuration);
                }
            }
            factory.setConfiguration(configuration);
        }
    }


    @Configuration
    @ConditionalOnBean(DataSourceConfiguration.CreatePrimaryDataSourceConfiguration.class)
    @EnableConfigurationProperties(MybatisPlusProperties.class)
    @ConditionalOnClass(MapperScannerConfigurer.class)
    public static class Mybatis1Configuration extends MybatisPlusAutoConfiguration {

        @Autowired
        public Mybatis1Configuration(MybatisPlusProperties properties,
                                     ObjectProvider<Interceptor[]> interceptorsProvider,
                                     ResourceLoader resourceLoader,
                                     ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                     ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                     ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider,
                                     ApplicationContext applicationContext) {
            super(properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider, mybatisPlusPropertiesCustomizerProvider, applicationContext);
        }

        // http://www.importnew.com/25940.html
        @Bean
        public static MapperScannerConfigurer createMapperScanner(
                ApplicationContext applicationContext) {
            String name = null;
            try {
                name = applicationContext.getEnvironment().
                        resolveRequiredPlaceholders("${flycat.datasource.primary.mybatis.mapper}");
            } catch (IllegalArgumentException e) {
            }
            String modulePackagesAsString = getScanPackages(name);
            LOGGER.info("Creating primary mybatis mapper, config:{}, module:{}", name, modulePackagesAsString);
            return MybatisUtils.createMapperConfigurer(modulePackagesAsString, SQL_SESSION_FACTORY_NAME_1);
        }

        @Bean(name = SQL_SESSION_FACTORY_NAME_1)
        public SqlSessionFactory createSqlSessionFactory(
                @Qualifier("primaryDataSource")
                        DataSource dataSource) throws Exception {
            return super.sqlSessionFactory(dataSource);
//            return MybatisUtils.createSessionFactory(dataSource);
        }

    }

    private static String getScanPackages(String name) {
        String modulePackagesAsString = getModulePackagesAsString(name);
        if (StringUtils.isEmpty(modulePackagesAsString)) {
            final Class<?> primarySource = SpringBootPlatform.getPrimarySource();
            modulePackagesAsString = primarySource.getPackage().getName();
        }
        return modulePackagesAsString;
    }


    @Configuration
    @ConditionalOnBean(DataSourceConfiguration.CreateSecondaryDataSourceConfiguration.class)
    @EnableConfigurationProperties(MybatisPlusProperties.class)
    @ConditionalOnClass(MapperScannerConfigurer.class)
    public static class MybatisSqlFactory2Configuration extends MybatisPlusAutoConfiguration {

        public static final String SQL_SESSION_FACTORY_NAME_2 = "sqlSessionFactory2";

        @Autowired
        public MybatisSqlFactory2Configuration(MybatisPlusProperties properties,
                                               ObjectProvider<Interceptor[]> interceptorsProvider,
                                               ResourceLoader resourceLoader,
                                               ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                               ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                               ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider, ApplicationContext applicationContext) {
            super(properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider, mybatisPlusPropertiesCustomizerProvider, applicationContext);
        }

        // http://www.importnew.com/25940.html
        @Bean
        @ConditionalOnProperty("flycat.datasource.secondary.mybatis.mapper")
        public static MapperScannerConfigurer createMapperScanner2(
                ApplicationContext applicationContext) {
            String name = null;
            try {
                name = applicationContext.getEnvironment().
                        resolveRequiredPlaceholders("${flycat.datasource.secondary.mybatis.mapper}");
            } catch (IllegalArgumentException e) {
            }
            LOGGER.info("Creating secondary mybatis mapper, {}", name);
            return MybatisUtils.createMapperConfigurer(name, SQL_SESSION_FACTORY_NAME_2);
        }

        @Bean(name = SQL_SESSION_FACTORY_NAME_2)
        public SqlSessionFactory createSqlSessionFactory(
                @Qualifier("secondaryDataSource") DataSource dataSource
        ) throws Exception {
            return super.sqlSessionFactory(dataSource);
//            return MybatisUtils.createSessionFactory(dataSource);
        }
    }
}
