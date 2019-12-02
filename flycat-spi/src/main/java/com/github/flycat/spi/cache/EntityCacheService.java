package com.github.flycat.spi.cache;

import com.github.flycat.exception.ResourceNotFoundException;
import com.github.flycat.util.page.Page;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class EntityCacheService<E> implements CacheOperation {
    private final CacheOperation cacheOperation;
    private final EntityCache<E> entityCache;

    public EntityCacheService(CacheOperation cacheOperation, Class<E> entityClass) {
        this.cacheOperation = cacheOperation;
        this.entityCache = new EntityCache<>(entityClass);
    }

    @Override
    public String createModuleNameByStackTrace(Type type) {
        return cacheOperation.createModuleNameByStackTrace(type);
    }

    @Override
    public Boolean removeCache(String module, String key) {
        return cacheOperation.removeCache(module, key);
    }

    @Override
    public Boolean removeCache(String module) {
        return cacheOperation.removeCache(module);
    }

    @Override
    public <P, T extends Number, K> CountMaps getCountMapsByModules(List<P> list, Function<? super P, K> mapper, Function<QueryKey<K>, Map<String, Map<String, T>>> callable, String... modules) throws CacheException {
        return cacheOperation.getCountMapsByModules(list, mapper, callable, modules);
    }

    @Override
    public <T extends Number, K> CountMaps getCountMapsByModules(List<K> keys, Function<QueryKey<K>, Map<String, Map<String, T>>> callable, String... modules) throws CacheException {
        return cacheOperation.getCountMapsByModules(keys, callable, modules);
    }

    @Override
    public long increaseCount(String module, Object key) throws CacheException {
        return cacheOperation.increaseCount(module, key);
    }

    @Override
    public long increaseCount(String module, Object key, Callable<Number> callable) throws CacheException {
        return cacheOperation.increaseCount(module, key, callable);
    }

    @Override
    public <T> T queryCacheObject(String module, Object key, Type type, Callable<T> callable) throws CacheException {
        return cacheOperation.queryCacheObject(module, key, type, callable);
    }

    @Override
    public <T> T queryCacheObject(String module, Object key, Type type, Callable<T> callable, int seconds) throws CacheException {
        return cacheOperation.queryCacheObject(module, key, type, callable, seconds);
    }

    @Override
    public <T> T queryAllCacheObjects(String module, Type type, Callable<T> callable) throws CacheException {
        return cacheOperation.queryAllCacheObjects(module, type, callable);
    }

    @Override
    public <T> T queryCacheObject(Object key, Type type, Callable<T> callable) throws CacheException {
        return cacheOperation.queryCacheObject(key, type, callable);
    }

    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, Object key, Callable<T> callable, int seconds) throws CacheException {
        return cacheOperation.queryNullableCacheObject(module, key, callable, seconds);
    }

    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, Object key, Callable<T> callable) throws CacheException {
        return cacheOperation.queryNullableCacheObject(module, key, callable);
    }

    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, String key, Type type, Callable<T> callable, int seconds) throws CacheException {
        return cacheOperation.queryNullableCacheObject(module, key, type, callable, seconds);
    }

    @Override
    public <T> Optional<T> queryNullableCacheObject(String module, String key, Type type, Callable<T> callable) throws CacheException {
        return cacheOperation.queryNullableCacheObject(module, key, type, callable);
    }

    @Override
    public boolean isValueRefreshed(String module, Object key, int seconds) throws CacheException {
        return cacheOperation.isValueRefreshed(module, key, seconds);
    }

    public <T> T queryCacheEntity(String key, Type type, Callable<T> callable) throws ResourceNotFoundException {
        return  queryNullableCacheObject(entityCache.byIdKey(),
                key,
                type,
                callable).orElseThrow(ResourceNotFoundException::new);

    }


    public E queryCacheEntity(String key,
                              Callable<E> callable) throws ResourceNotFoundException {
        return  queryNullableCacheObject(entityCache.byIdKey(),
                entityCache.getEntityClass(),
                callable).orElseThrow(ResourceNotFoundException::new);
    }

    public void removeQueryEntityAndPageCache(String id) {
        entityCache.removeQueryEntityAndPageCache(cacheOperation, id);
    }

    public void removeQueryByIdCache(String id) {
        entityCache.removeQueryByIdCache(cacheOperation, id);
    }


    public Page<E> queryListPage(Callable<Page<E>> listQuery, Function<String, List<Map<String, Integer>>> countQuery, Integer pageNum, String... countColumns) {
        return entityCache.queryListPage(cacheOperation, listQuery, countQuery, pageNum, countColumns);
    }
}
