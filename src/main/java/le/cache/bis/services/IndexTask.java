/**
 * 
 */
package le.cache.bis.services;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import le.cache.bis.services.impl.PrimaryKeyType;

/**
 * 
 * @author ledwinson
 *
 * @param <P> The primary Key type.
 * @param <V> The root entity type on the cache.
 */
public interface IndexTask<P, V> extends Callable<Indexer<P>> {

    void init(Cache<P, V> cache, Indexer<P> indexer, Method method, boolean isCaseSensitive, PrimaryKeyType<P> type);
    
    
}
