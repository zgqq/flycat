package com.github.bootbox.server.event;

import com.google.common.eventbus.EventBus;

public class EventManager {
    private static final EventBus EVENT_BUS = new EventBus();

    public static void register(Object obj) {
        EVENT_BUS.register(obj);
    }

    public static void unregister(Object obj) {
        EVENT_BUS.unregister(obj);
    }

    public static void post(Object obj) {
        EVENT_BUS.post(obj);
    }
}
