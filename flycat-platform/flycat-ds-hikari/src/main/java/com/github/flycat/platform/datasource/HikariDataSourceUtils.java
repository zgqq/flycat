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
package com.github.flycat.platform.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * Created by zgq on 17-3-15.
 */
public class HikariDataSourceUtils {

    public static HikariDataSource createDataSource(DataSourceConfig dataSourceConfig, HikariConfiguration configuration) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceConfig.getUrl());
        config.setUsername(dataSourceConfig.getUsername());
        config.setPassword(dataSourceConfig.getPassword());
        config.setDriverClassName(dataSourceConfig.getDriverClassName());
        config.setConnectionInitSql(dataSourceConfig.getInitSQL());

        if (configuration != null) {
            config.setConnectionTimeout(configuration.getConnectionTimeout());
            config.setIdleTimeout(configuration.getIdleTimeout());
            config.setLeakDetectionThreshold(configuration.getLeakDetectionThreshold());
            config.setConnectionInitSql(configuration.getInitSQL());
        }

        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }
}
