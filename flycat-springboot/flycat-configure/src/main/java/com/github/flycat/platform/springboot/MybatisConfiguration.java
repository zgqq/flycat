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
import com.github.flycat.db.mybatis.MybatisUtils;
import com.github.flycat.util.StringUtils;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

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
                                     ObjectProvider<List<ConfigurationCustomizer>>
                                             configurationCustomizersProvider,
                                     ObjectProvider<List<MybatisPlusPropertiesCustomizer>>
                                             mybatisPlusPropertiesCustomizerProvider,
                                     ApplicationContext applicationContext) {
            super(properties, interceptorsProvider, resourceLoader,
                    databaseIdProvider, configurationCustomizersProvider,
                    mybatisPlusPropertiesCustomizerProvider, applicationContext);
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
        if (StringUtils.isBlank(modulePackagesAsString)) {
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
                                               ObjectProvider<List<ConfigurationCustomizer>>
                                                       configurationCustomizersProvider,
                                               ObjectProvider<List<MybatisPlusPropertiesCustomizer>>
                                                       mybatisPlusPropertiesCustomizerProvider,
                                               ApplicationContext applicationContext) {
            super(properties, interceptorsProvider, resourceLoader,
                    databaseIdProvider, configurationCustomizersProvider,
                    mybatisPlusPropertiesCustomizerProvider, applicationContext);
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
