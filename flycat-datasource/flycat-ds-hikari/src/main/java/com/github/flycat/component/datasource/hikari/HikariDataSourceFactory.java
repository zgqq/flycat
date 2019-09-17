package com.github.flycat.component.datasource.hikari;

import com.github.flycat.component.datasource.DataSourceConfig;
import com.github.flycat.component.datasource.DataSourceFactory;

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
