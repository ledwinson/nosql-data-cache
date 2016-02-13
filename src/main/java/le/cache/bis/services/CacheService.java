/**
 * 
 */
package le.cache.bis.services;

import le.cache.bis.services.impl.PrimaryKeyType;
import le.cache.util.CacheState;


/**
 * 
 * @author ledwinson
 *
 * @param <V> the EntityType loaded from data source to cache.
 * @param <S> the Snapshot interface type used for querying the snapshot.
 * @param <P> the primary key data type on the loaded entity.
 */
public interface CacheService<V, S extends Snapshot, P> {

    /**
     * Cache Entire  Elements returned from  data source
     * @param dataSource the dataSource to load data from
     * @param isCaseSensitive
     * @param type
     * @param snapshotInterfaceType
     */
    void cache(DataSourceResolver<V> dataSource, boolean isCaseSensitive, PrimaryKeyType<P> type, Class<S> snapshotInterfaceType);
    
    /**
     * 
     * @return The Snapshot if the cache is not corrupted and is ready.
     */    
    S getSnapshot();
    
    /**
     * 
     * @return the current state of the cache.
     */
    CacheState getStatus();

    /**
     * Cache Individual Elements
     * @param data
     * @param isCaseSensitive
     * @param type
     * @param snapshotInterfaceType
     */
    void cache(V data, boolean isCaseSensitive, PrimaryKeyType<P> type, Class<S> snapshotInterfaceType);
    
    /**
     * 
     * @param data the element to remove
     * @param type The primary key type
     */
    void removeElement(V data, PrimaryKeyType<P> type);
}
