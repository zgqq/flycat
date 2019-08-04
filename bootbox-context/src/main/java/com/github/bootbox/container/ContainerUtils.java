package com.github.bootbox.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public final class ContainerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerUtils.class);

    private static final AtomicReference<ApplicationContainer> CONTAINER_HOLDER = new AtomicReference<>();

    private ContainerUtils() {
    }

    public static synchronized void setContainerHolder(ApplicationContainer containerHolder) {
        if (containerHolder == null) {
            throw new UnsupportedOperationException("Container holder is null");
        }
        if (ContainerUtils.CONTAINER_HOLDER.get() != null) {
            throw new UnsupportedOperationException("Already set container");
        }
        ContainerUtils.CONTAINER_HOLDER.set(containerHolder);
        LOGGER.info("Set container holder, container:{}", containerHolder);
    }

    public static synchronized ApplicationContainer getContainerHolder() {
        return ContainerUtils.CONTAINER_HOLDER.get();
    }
}
