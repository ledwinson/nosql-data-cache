package le.cache.bis.services;

import le.cache.bis.services.impl.PrimaryKeyType;

public interface IndexService<P,V,S> {

    /**
     * Will do each indexing parallel from the findBy methods of the registered {@link Snapshot} interface.
     * @param cache the cache to get the data from and to store the index details back.
     * @param isCaseSensitive if the query on index will be case sensitive or not.
     * @param type the primary key on the root entity cached.
     * @param snapshotInterfaceType The interface to query the cache based on this index.
     */
    void index(Cache<P, V> cache, boolean isCaseSensitive, PrimaryKeyType<P> type, Class<S> snapshotInterfaceType);

}
