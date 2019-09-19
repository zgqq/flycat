package com.github.flycat.db.mybatis;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
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
        return sqlSessionFactoryBean.getObject();
    }
}
