/**
 * 
 */
package le.cache.bis.services.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import le.cache.bis.services.Cache;
import le.cache.bis.services.Indexer;
import le.cache.bis.services.ProxyInvocationtarget;
import le.cache.bis.services.Snapshot;
import le.cache.bis.services.exception.MultipleElementsInResultSetException;
import le.cache.bis.services.exception.SnapshotException;

/**
 * @author ledwinson
 *
 */
@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
class ProxyInvocationTargetImpl<K,V> implements ProxyInvocationtarget<K,V> {

    private Cache<K, V> cache;
    
    private final String releaseMethodName = "release";
    private final String sizeMethodName = "size";
    private final Logger logger = Logger.getLogger(ProxyInvocationTargetImpl.class.getName());
    
    @Override
    public void registerCache(Cache<K, V> cache) {
        this.cache = cache;
    }
  
    @Override
    public Object invoke(Object proxyObject, Method method, Object[] arguments) throws Throwable {
        
        if(cache == null) {
            throw new SnapshotException("Cache is not initialized for this target");
        }
        
        if(!method.getName().startsWith(Snapshot.METHOD_PREFIX)) {
            if(method.getName().equals(releaseMethodName)) {
                return cache.release();
            } else if(method.getName().equals(sizeMethodName)) {
                return cache.getData().size();
            }

            logger.error("Ignoring Invalid method pattern " + method.getName());
            return null;
        }             
        
        if(cache.isInvalidated()) {
            throw new SnapshotException("This cache is been released already.");
        }
        
        final Indexer<K> indexer = cache.getIndexer(method.getName());
        if(indexer == null) {
            throw new SnapshotException("Indexer doesn't exists for method " + method.getName());
        }
        
        final Class<?> returnType = method.getReturnType();
        final List<K> keysList = indexer.getKeys(method.getName(), arguments);
        final List<V> results = cache.get(keysList);
        
        if(returnType.isAssignableFrom(List.class)) {
            return results;
        } else if (returnType.isAssignableFrom(Set.class)){
            final Set<V> values = new HashSet<>(results == null ? new ArrayList<V>(1) : results);
            return values;
        } else if (returnType.isAssignableFrom(Collection.class)){
            return results;
        }
        
        if(results == null || results.isEmpty()) {
            return null;
        }
        
        final int first = 0;
        if(results.size() > 1) {
            logger.error("Snapshot: more than one elememnt but expected only one in the query for the type " + results.get(first).getClass().getName());
            logger.error("These are the duplicate records [primary key] " + keysList);
            throw new MultipleElementsInResultSetException("More than one elements found in the Snashot...");
        }
        
        
        return results.get(first);
    }
}
