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
package com.github.flycat.db.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;

public class MybatisUtils {

    public static MapperScannerConfigurer createMapperConfigurer(String basePackage,
                                                                 String sqlSessionFactoryName) {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage(basePackage);
        configurer.setAnnotationClass(Mapper.class);
        configurer.setSqlSessionFactoryBeanName(sqlSessionFactoryName);
        return configurer;
    }

    public static SqlSessionFactory createSessionFactory(DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        SqlSessionFactory object = sqlSessionFactoryBean.getObject();
        object.getConfiguration().setMapUnderscoreToCamelCase(true);
        return object;
    }
}
