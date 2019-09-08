/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.autoconfigure;

import com.github.flycat.db.mybatis.MybatisUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


/**
 */
@Configuration
@ConditionalOnClass(MapperScannerConfigurer.class)
public class MybatisConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisConfiguration.class);
    public static final String SQL_SESSION_FACTORY_NAME_1 = "sqlSessionFactory1";


    @Configuration
//    @AutoConfigureAfter(CreatePrimaryDataSourceConfiguration.class)
//    @ConditionalOnBean(CreatePrimaryDataSourceConfiguration.class)
    @ConditionalOnBean(name = "primaryDataSource")
    @ConditionalOnClass(MapperScannerConfigurer.class)
    public static class Mybatis1Configuration {

        // http://www.importnew.com/25940.html
        @Bean
        public MapperScannerConfigurer createMapperScanner(
                ApplicationContext applicationContext) {
            String name = applicationContext.getEnvironment().
                    resolvePlaceholders("${flycat.datasource.primary.mybatis.mapper}");
            LOGGER.info("Creating primary mybatis mapper, {}", name);
            return MybatisUtils.createMapperConfigurer(name, SQL_SESSION_FACTORY_NAME_1);
        }


        @Bean(name = SQL_SESSION_FACTORY_NAME_1)
        public SqlSessionFactory createSqlSessionFactory(
                @Qualifier("primaryDataSource")
                        DataSource dataSource) throws Exception {
            return MybatisUtils.createSessionFactory(dataSource);
        }
    }


    @Configuration
//    @ConditionalOnBean(CreateSecondaryDataSourceConfiguration.class)
//    @AutoConfigureAfter(CreateSecondaryDataSourceConfiguration.class)
    @ConditionalOnBean(name = "secondaryDataSource")
    @ConditionalOnClass(MapperScannerConfigurer.class)
    public static class MybatisSqlFactory2Configuration {

        public static final String SQL_SESSION_FACTORY_NAME_2 = "sqlSessionFactory2";

        // http://www.importnew.com/25940.html
        @Bean
        public MapperScannerConfigurer createMapperScanner2(
                ApplicationContext applicationContext) {
            String name = applicationContext.getEnvironment().
                    resolvePlaceholders("${flycat.datasource.secondary.mybatis.mapper}");
            LOGGER.info("Creating secondary mybatis mapper, {}", name);
            return MybatisUtils.createMapperConfigurer(name, SQL_SESSION_FACTORY_NAME_2);
        }

        @Bean(name = SQL_SESSION_FACTORY_NAME_2)
        public SqlSessionFactory createSqlSessionFactory(
                @Qualifier("secondaryDataSource") DataSource dataSource
        ) throws Exception {
            return MybatisUtils.createSessionFactory(dataSource);
        }
    }
}
