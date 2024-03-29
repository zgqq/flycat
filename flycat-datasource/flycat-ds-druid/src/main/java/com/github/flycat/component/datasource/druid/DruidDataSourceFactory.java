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
package com.github.flycat.component.datasource.druid;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.github.flycat.component.datasource.DataSourceConfig;
import com.github.flycat.component.datasource.DataSourceFactory;
import com.google.common.collect.Lists;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import javax.sql.DataSource;

@Named
@Singleton
public class DruidDataSourceFactory implements DataSourceFactory {


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


        Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
        WallFilter wallFilter = new WallFilter();
        StatFilter statFilter = new StatFilter();

        statFilter.setLogSlowSql(true);
        statFilter.setSlowSqlMillis(2000);
        final WallConfig wallConfig = new WallConfig();
        wallConfig.setDeleteAllow(false);
        wallConfig.setDropTableAllow(false);
        wallFilter.setConfig(wallConfig);
        wallFilter.setDbType(JdbcUtils.getDbType(dataSourceConfig.getUrl()).getDb());

        druidDataSource.getProxyFilters().add(slf4jLogFilter);
        druidDataSource.getProxyFilters().add(statFilter);
        druidDataSource.getProxyFilters().add(wallFilter);
        return druidDataSource;
    }

}
