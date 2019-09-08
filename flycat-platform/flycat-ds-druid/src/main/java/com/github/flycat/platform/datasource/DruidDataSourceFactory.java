package com.github.flycat.platform.datasource;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.collect.Lists;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Named
@Singleton
public class DruidDataSourceFactory implements DataSourceFactory {
    private final Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
    private final WallFilter wallFilter = new WallFilter();
    private final StatFilter statFilter = new StatFilter();

    {
        statFilter.setLogSlowSql(true);
        statFilter.setSlowSqlMillis(2000);
        final WallConfig wallConfig = new WallConfig();
        wallConfig.setDeleteAllow(false);
        wallConfig.setDropTableAllow(false);
        wallFilter.setConfig(wallConfig);
    }



    @Override
    public DataSource createDataSource(DataSourceConfig dataSourceConfig) {
        final DruidDataSource druidDataSource = new DruidDataSource() {
            /**
             * Ignore the 'maxEvictableIdleTimeMillis < minEvictableIdleTimeMillis' validate,
             * it will be validated again in {@link DruidDataSource#init()}.
             *
             * for fix issue #3084, #2763
             *
             * @since 1.1.14
             */
            @Override
            public void setMaxEvictableIdleTimeMillis(long maxEvictableIdleTimeMillis) {
                try {
                    super.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
                } catch (IllegalArgumentException ignore) {
                    super.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
                }
            }
        };
        druidDataSource.setUrl(dataSourceConfig.getUrl());
        druidDataSource.setUsername(dataSourceConfig.getUsername());
        druidDataSource.setPassword(dataSourceConfig.getPassword());
        druidDataSource.setDriverClassName(dataSourceConfig.getDriverClassName());
        druidDataSource.setConnectionInitSqls(Lists.newArrayList(dataSourceConfig.getInitSQL()));

        druidDataSource.getProxyFilters().add(slf4jLogFilter);
        druidDataSource.getProxyFilters().add(statFilter);
        druidDataSource.getProxyFilters().add(wallFilter);
        return druidDataSource;
    }

}
