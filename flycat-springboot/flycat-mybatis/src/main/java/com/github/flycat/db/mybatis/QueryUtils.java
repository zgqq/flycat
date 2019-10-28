package com.github.flycat.db.mybatis;

import java.util.List;

public class QueryUtils {

    public static String createIdString(List idList) {
        StringBuilder builder = new StringBuilder();
        if (idList == null || idList.isEmpty()) {
            return null;
        }
        for (int i = 0; i < idList.size(); i++) {
            if (i == idList.size() - 1) {
                builder.append(idList.get(i));
            } else {
                builder.append(idList.get(i) + ",");
            }
        }
        return builder.toString();
    }
}
