/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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


