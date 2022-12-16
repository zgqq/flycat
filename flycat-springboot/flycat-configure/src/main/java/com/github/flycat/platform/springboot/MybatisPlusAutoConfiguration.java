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
import com.github.flycat.util.StringUtils;
import com.google.common.collect.Lists;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class MybatisPlusAutoConfiguration {


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
                                            ObjectProvider<List<ConfigurationCustomizer>>
                                                    configurationCustomizersProvider,
                                            ObjectProvider<List<MybatisPlusPropertiesCustomizer>>
                                                    mybatisPlusPropertiesCustomizerProvider,
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
//                IKeyGenerator keyGenerator = this.applicationContext.getBean(IKeyGenerator.class);
                ObjectProvider<IKeyGenerator> beanProvider = this.applicationContext.getBeanProvider(IKeyGenerator.class);
                ArrayList<IKeyGenerator> iKeyGenerators = Lists.newArrayList(beanProvider.iterator());
                globalConfig.getDbConfig().setKeyGenerators(iKeyGenerators);
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
