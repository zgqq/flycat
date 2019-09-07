package com.github.flycat.platform.datasource;

import javax.sql.DataSource;

public interface DataSourceFactory {
    DataSource createDataSource(DataSourceConfig dataSourceConfig);
}
