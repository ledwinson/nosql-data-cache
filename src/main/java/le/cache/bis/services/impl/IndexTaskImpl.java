/**
 * 
 */
package le.cache.bis.services.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import le.cache.bis.services.Cache;
import le.cache.bis.services.IndexTask;
import le.cache.bis.services.Indexer;
import le.cache.bis.services.exception.SnapshotException;
import le.cache.util.Property;
import le.cache.util.Utility;

/**
 * @author ledwinson
 *
 */
@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
class IndexTaskImpl<P,V> implements IndexTask<P, V> {
    
    private Method method;
    private boolean isCaseSensitive = true;
    private PrimaryKeyType<P> type;
    private Cache<P, V> cache;
    private Indexer<P> indexer;
        
    @Override
    public Indexer<P> call() throws Exception {
        if(Utility.isAnyNull(new Object[]{method, type, cache, indexer})) {
            throw new SnapshotException("Call the init method and set the values to process.");
        }
        
        final Annotation[][] anotations = method.getParameterAnnotations();
        if(anotations.length == 0) {
            return null;
        }
        final List<Property> parameters = new ArrayList<>(anotations.length);
        for(int property = 0; property < anotations.length; property++) {
            final Annotation annotation = anotations[property][0];
            if(Property.class.isAssignableFrom(annotation.getClass())) {
                parameters.add(((Property)annotation));
            }
        }

        indexer.setCaseSensitiveSearch(isCaseSensitive)
        .setPrimaryKeyType(type)
        .setQuery(method.getName())
        .indexBy(parameters, cache);

        return indexer;
    }

    @Override
    public void init(Cache<P, V> cache, Indexer<P> indexer ,Method method, boolean isCaseSensitive, PrimaryKeyType<P> type) {
        this.method = method;
        this.isCaseSensitive = isCaseSensitive;
        this.type = type;
        this.cache = cache;
        this.indexer = indexer;
    }

}
