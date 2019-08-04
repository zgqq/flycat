package com.github.bootbox.util;

import java.util.Collection;

public final class CollectionUtils {

    public static <T> boolean greater(Collection<T>
                                              collection, int size
    ) {
        if (collection == null) {
            return false;
        }
        return collection.size() > size;
    }

    public static <T> boolean isNotEmpty(Collection<T>
                                                 collection) {
        return collection != null && !collection.isEmpty();
    }
}
