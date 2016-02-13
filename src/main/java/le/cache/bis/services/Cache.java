package le.cache.bis.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import le.cache.bis.services.impl.PrimaryKeyType;

/**
 * 
 * @author ledwinson
 *
 * @param <K> the primary key
 * @param <V> the real entity object
 */
public interface Cache<K,V> {
   
    void cache(Collection<V> data, PrimaryKeyType<K> type);
    
    void cacheIndex(Indexer<K> indexer);
    
    V get(K key);
    
    V remove(K key);
    
    Set<String> getIndexNames();
    
    Map<K, V> getData();
    
    Indexer<K> getIndexer(String name);
    
    List<V> get(List<K> keysList);
    
    boolean isEmpty();

    boolean release();
    
    Boolean isInvalidated();

    void reset();

    /**
     * @param data the data to cache.
     * @param type the primary key type
     * @return the existing value if any in the cache for the primary key of the object.
     */
    V cache(V data, PrimaryKeyType<K> type);
    
    Collection<Indexer<K>> getAllIndexers();
}
