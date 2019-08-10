package com.github.bootbox.cache.ehcache;

import org.ehcache.config.Builder;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;

import javax.cache.configuration.Configuration;
import java.time.Duration;

public final class EhcacheUtils {

    public static <K, V> Configuration<K, V>
    createEh107CacheConfiguration(Class<K> keyType,
                                  Class<V> valueType,
                                  Builder<? extends ResourcePools> resourcePoolsBuilder) {
        return Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        keyType, valueType, resourcePoolsBuilder
                ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                        Duration.ofMinutes(10)
                )).build()
        );
    }

    public static <K, V> Configuration<K, V> createEh107CacheConfiguration(Class<K> keyType,
                                                                           Class<V> valueType) {
        return createEh107CacheConfiguration(keyType, valueType,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(2000, EntryUnit.ENTRIES)
                        .offheap(20, MemoryUnit.MB)
        );
    }
}


