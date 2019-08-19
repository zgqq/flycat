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
package com.github.flycat.util.generator.sql;

import com.github.flycat.util.generator.docs.ApiModelField;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTableGenerator {

    private static StringBuilder docsString;

    public static String createTableSql(List<Class<?>> classList) {
        docsString = new StringBuilder();
        List<String> tableSqls = new ArrayList<>();
        for (Class<?> clazz : classList) {
            final TableModel annotation = clazz.getAnnotation(TableModel.class);
            List<String> otherTablesPrefix = new ArrayList<>();
            if (annotation != null) {
                final ArrayList<String> objects = Lists.newArrayList();

                final Class<?>[] classes = annotation.manyToOne();
                for (Class<?> aClass : classes) {
                    final TableModel annotation1 = aClass.getAnnotation(TableModel.class);
                    if (annotation1 != null && !annotation1.alias().isEmpty()) {
                        objects.add(annotation1.alias());
                    } else {
                        final String alias = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz.getSimpleName());
                        objects.add(alias);
                    }
                }
                otherTablesPrefix.addAll(Lists.newArrayList(objects));
            }

            final Field[] fields = clazz.getDeclaredFields();
            final String table = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
            String createTable = "DROP TABLE IF EXISTS `" + table + "`;\n create table `" + table + "`( \n";
            String priKeyName = null;
            List<String> columns = new ArrayList<>();
            for (Field field : fields) {
                String fieldName = field.getName();
                final ApiModelField apiModelProperty = field.getAnnotation(ApiModelField.class);
                final String name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);

                String columnSql = null;
                String columnName = "`" + name + "`";
                if ("id".equals(name)) {
                    columnSql = "`id` int(10) unsigned NOT NULL AUTO_INCREMENT";
                    priKeyName = columnName;
                } else if (name.contains("status")) {
                    columnSql = columnName + " tinyint(2) DEFAULT '0'";
                } else if (name.contains("description")) {
                    columnSql = columnName + " varchar(2048) DEFAULT NULL";
                } else {
                    boolean jump = false;
                    for (String tablesPrefix : otherTablesPrefix) {
                        System.out.println("matching prefix " + tablesPrefix + ", name " + name);
                        if (name.startsWith(tablesPrefix)
                                && !name.equals(tablesPrefix + "_id")) {
                            jump = true;
                            break;
                        }
                    }
                    if (jump) {
                        continue;
                    }

                    final Class<?> type = field.getType();
                    if (Integer.class.equals(type)) {
                        columnSql = columnName + " int(10) DEFAULT NULL";
                    } else if (String.class.equals(type)) {
                        columnSql = columnName + " varchar(256) DEFAULT NULL";
                    } else if (Date.class.equals(type)) {
                        columnSql = columnName + " datetime NULL";
                    }
                }
                if (columnSql != null) {
                    if (apiModelProperty != null) {
                        final String value = apiModelProperty.value();
                        columnSql += " COMMENT '" + value + "'";
                    }
                    columnSql += ",";
                    columns.add(columnSql);
                }
            }

            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                if (columns.size() == i + 1) {
                    if (priKeyName == null) {
                        column = column.replace(",", "");
                    }
                }
                createTable += "\t" + column + "\n";
            }

            if (priKeyName != null) {
                createTable += "\tPRIMARY KEY (`id`) \n";
            }
            createTable += ") ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;\n";
            tableSqls.add(createTable);
        }

        for (String tableSql : tableSqls) {
            appendDocs(tableSql);
        }
        return docsString.toString();
    }

    private static void appendDocs(String titleMd) {
        docsString.append(titleMd + "\n");
    }
}
