package com.github.flycat.component.datasource;

import javax.sql.DataSource;

public interface DataSourceFactory {
    DataSource createDataSource(DataSourceConfig dataSourceConfig);
}
