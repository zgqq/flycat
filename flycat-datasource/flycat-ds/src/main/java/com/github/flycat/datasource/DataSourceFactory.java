package com.github.flycat.datasource;

import javax.sql.DataSource;

public interface DataSourceFactory {
    DataSource createDataSource(DataSourceConfig dataSourceConfig);
}
