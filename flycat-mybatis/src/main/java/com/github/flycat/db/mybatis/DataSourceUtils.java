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
package com.github.flycat.db.mybatis;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;


/**
 * Created by zgq on 17-3-15.
 */
public class DataSourceUtils {

    public static HikariDataSource createDataSource(String url, String username, String password,
                                                    String driverClassName,
                                                    HikariConfiguration configuration) {
        return new HikariDataSource(createConfig(url, username, password, driverClassName, configuration));
    }

    public static HikariConfig createConfig(String url, String username, String password, String driverClassName,
                                            HikariConfiguration configuration) {
        HikariConfig config = createConfig(url, username, password, driverClassName);
        config.setConnectionTimeout(configuration.getConnectionTimeout());
        config.setIdleTimeout(configuration.getIdleTimeout());
        config.setLeakDetectionThreshold(configuration.getLeakDetectionThreshold());
        if (configuration.getInitSQL() != null) {
            config.setConnectionInitSql(configuration.getInitSQL());
        }
        return config;
    }

    public static HikariConfig createConfig(String url, String username, String password, String driverClassName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        return config;
    }

    public static DataSource createDataSource(String url, String username, String password, String driverClassName) {
        HikariDataSource ds = new HikariDataSource(createConfig(url, username, password, driverClassName));
        return ds;
    }

    public static MapperScannerConfigurer createMapperConfigurer(String basePackage, String sqlSessionFactoryName) {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage(basePackage);
//        configurer.setAnnotationClass(Mapper.class);
        configurer.setSqlSessionFactoryBeanName(sqlSessionFactoryName);
        return configurer;
    }

    public static SqlSessionFactory createSessionFactory(DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean.getObject();
    }
}
