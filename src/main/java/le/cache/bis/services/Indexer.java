package le.cache.bis.services;

import java.util.List;

import le.cache.bis.services.impl.PrimaryKeyType;
import le.cache.util.Property;

/**
 * 
 * @author ledwinson
 * <br/>
 * The indexer Service that indexes the cache based on the Snapshot Interface Queries provided.
 *
 * @param <P>
 */
public interface Indexer<P> {

    /**
     * Index all the elements in the cache based on the given properties list.
     * @param arguments the properties to index the cache
     * @param cache the cache with cached data in it.
     */
    <V> void indexBy(List<Property> arguments, Cache<P, V> cache);
    
    /**
     * Set the query name for which this index is going to be invoked.
     * @param queryName the query name
     * @return this reference
     */
    Indexer<P> setQuery(String queryName);
    
    /**
     * Set the primary key type of the root element in the cache.
     * @param keyType primary key type
     * @return this reference
     */
    Indexer<P> setPrimaryKeyType(PrimaryKeyType<P> keyType);
    
    /**
     * 
     * @param isCaseSensitive set if the search is case sensitive or not.
     * @return this reference
     */
    Indexer<P> setCaseSensitiveSearch(boolean isCaseSensitive);
    
    /**
     * 
     * @return the name of the index if set already.
     */
    String getName();
    
    /**
     * 
     * @param name the name of the index
     * @param arguments The index property values from the new object to look up.
     * @return A list of primary keys : The primary keys of the root objects cached for this index. 
     * <br/> If you have multiple elements for the same index key then all values are returned.
     */
    
    List<P> getKeys(String name, Object[] arguments);
    
    /**
     * 
     * @return the Query parameters used to index the data for this query.
     */
    Property[] getQueryParams();

    /**
     * Once all the elements are indexed to the cache and if a new element is added to the existing cache, we can call this method directly to index the new element only.
     * @param thePrimaryKeyOfNewElementInCache the primary key of the new element that is added to the cache.
     * @param objectOnWhichTheMethodTobeInvoked the the actual element to index
     */
    void indexElementToTheExistingIndexer(P thePrimaryKeyOfNewElementInCache, Object objectOnWhichTheMethodTobeInvoked);
    
    /**
     * This method allows an element removed from cache if it exists in the cache.
     * @param thePrimaryKeyOfElementInCache
     * @param objectOnWhichTheMethodTobeInvoked The real object from which we will work out hashcode for indexing ...
     */
    void removeElementIfPresent(P thePrimaryKeyOfElementInCache, Object objectOnWhichTheMethodTobeInvoked);
}
