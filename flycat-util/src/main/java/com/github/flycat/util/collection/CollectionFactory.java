package com.github.flycat.util.collection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Map;

public class CollectionFactory {


    public static <E> ArrayList<E> createArrayList(E... elements) {
        return Lists.newArrayList(elements);
    }

    public static <K, V> Map<K, V> createImmutableMap(K k1, V v1) {
        return ImmutableMap.of(k1, v1);
    }

    public static <K, V> Map<K, V> createImmutableMap(K k1, V v1, K k2, V v2) {
        return ImmutableMap.of(k1, v1, k2, v2);
    }

    public static <K, V> Map<K, V> createImmutableMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return ImmutableMap.of(k1, v1, k2, v2, k3, v3);
    }

    public static <K, V> Map<K, V> createImmutableMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4);
    }
}
