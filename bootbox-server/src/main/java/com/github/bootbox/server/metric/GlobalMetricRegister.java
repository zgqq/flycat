package com.github.bootbox.server.metric;

import com.codahale.metrics.MetricRegistry;

public class GlobalMetricRegister {
    private static final MetricRegistry REGISTRY = new MetricRegistry();

    public static MetricRegistry getRegistry() {
        return REGISTRY;
    }
}
