package com.github.flycat.spi.json;

public interface SerializationConfig {
    void includeNonNull();

    void includeNonEmpty();
}
