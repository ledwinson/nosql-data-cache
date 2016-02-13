package le.cache.bis.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import le.cache.bis.services.Cache;
import le.cache.bis.services.Indexer;
import le.cache.bis.services.exception.SnapshotException;

@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
class CacheImpl<K, V> implements Cache<K, V> {

    private static final Object[] EMPTY_ARRAY = new Object[]{};    
    private final Logger logger = Logger.getLogger(CacheImpl.class.getName());
    
    private final Map<K, V> cache = new HashMap<>();
    
    private final Map<String, Indexer<K>> index = new HashMap<>();
    
    private Boolean invalidated = false;
        
    @Override
    public void cache(Collection<V> data, PrimaryKeyType<K> type) {
        for(V element: data) {
            try {
                cache(element, type);
            } catch (Exception e) {
                logger.error("Could not get primary key value for the cached object", e);
                throw new SnapshotException(e);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public V cache(V data, PrimaryKeyType<K> type) {
        try {
            return cache.put((K)type.getMethod().invoke(data, EMPTY_ARRAY), data);
        } catch (Exception e) {
            logger.error("Could not get primary key value for the cached object", e);
            throw new SnapshotException(e);
        }
    }
    

    @Override
    public void cacheIndex(Indexer<K> indexer) {
        index.put(indexer.getName(), indexer);
    }
    
    @Override
    public V get(K key) {
        return cache.get(key);
    }
    
    @Override
    public Set<String> getIndexNames() {
        return index.keySet();
    }

    @Override
    public Map<K, V> getData() {
        return Collections.unmodifiableMap(cache);
    }
    
    @Override
    public Indexer<K> getIndexer(String name) {        
        return index.get(name);
    }
    
    @Override
    public List<V> get(List<K> keysList) {
        if(keysList == null || keysList.isEmpty()) {
            return new ArrayList<V>(1);
        }

        final List<V> values = new ArrayList<>(keysList.size());
        
        for(K key: keysList) {
            final V value = cache.get(key);
            if(value != null) {
                values.add(value);
            }
        }
        return values;
    }

    @Override
    public boolean isEmpty() {
       return this.cache.isEmpty();
    }

    @Override
    public boolean release() {
        invalidated = true;
        this.cache.clear();
        this.index.clear();
        return true;
    }
    
    @Override
    public Boolean isInvalidated() {        
        return invalidated;
    }

    @Override
    public void reset() {
        invalidated = false;
        this.cache.clear();
        this.index.clear();        
    }
    
    @Override
    public Collection<Indexer<K>> getAllIndexers() {
        return index.values();
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }    
}
