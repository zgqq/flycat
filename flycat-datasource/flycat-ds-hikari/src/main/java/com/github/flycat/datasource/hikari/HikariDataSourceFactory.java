package com.github.flycat.datasource.hikari;

import com.github.flycat.datasource.DataSourceConfig;
import com.github.flycat.datasource.DataSourceFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Named
@Singleton
public class HikariDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource createDataSource(DataSourceConfig dataSourceConfig) {
        return HikariDataSourceUtils.createDataSource(
                dataSourceConfig, null
        );
    }
}
