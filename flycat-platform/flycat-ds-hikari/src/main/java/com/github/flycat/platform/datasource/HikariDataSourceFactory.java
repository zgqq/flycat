package com.github.flycat.platform.datasource;

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
