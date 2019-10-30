package com.github.flycat.spi.cache;

import java.util.ArrayList;
import java.util.List;

public class QueryKey<K> {
    private final String[] modules;
    private final List<K> keys;
    private final String keysString;

    public QueryKey(String[] modules, List<K> keys) {
        this.modules = modules;
        this.keys = keys;
        this.keysString = createKeysString(keys);
    }

    public String[] getModules() {
        return modules;
    }

    public static String getSubmoduleName(String module) {
        if (module.contains("#")) {
            return module.split("#")[1];
        }
        return module;
    }

    public String[] getSubmodules() {
        final ArrayList<String> submodules = new ArrayList<>();
        for (String module : modules) {
            submodules.add(getSubmoduleName(module));
        }
        return submodules.toArray(new String[]{});
    }

    public List<K> getKeys() {
        return keys;
    }

    public String getKeysString() {
        return keysString;
    }

    public static String createKeysString(List idList) {
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
