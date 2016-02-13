/**
 * 
 */
package le.cache.bis.services.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import le.cache.bis.services.Cache;
import le.cache.bis.services.IndexService;
import le.cache.bis.services.IndexTask;
import le.cache.bis.services.Indexer;
import le.cache.bis.services.Snapshot;
import le.cache.bis.services.exception.SnapshotException;
import le.cache.util.ReflectionUtil;

/**
 * @author ledwinson
 *
 */
@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
class IndexServiceImpl<P, V, S> implements IndexService<P, V, S> {
       
    @Autowired
    private ApplicationContext context;
       
    private final Logger logger = Logger.getLogger(IndexServiceImpl.class.getName());
    
    
    @SuppressWarnings("unchecked")
    @Override
    public void index(Cache<P, V> cache, boolean isCaseSensitive, PrimaryKeyType<P> type, Class<S> snapshotInterfaceType) {
        final Method[] methods = ReflectionUtil.getAllMethods(snapshotInterfaceType);
        final ExecutorService executor = Executors.newCachedThreadPool(); // there won't be too many indices
        final List<IndexTask<P,V>> tasks = new ArrayList<>();
        for(Method method: methods) {
            if(method.getName().startsWith(Snapshot.METHOD_PREFIX)) {
                final Indexer<P> indexer = context.getBean(Indexer.class);
                final IndexTask<P, V> task = context.getBean(IndexTask.class);
                task.init(cache, indexer, method, isCaseSensitive, type);
                tasks.add(task);
            }
        }
        
        if(tasks.isEmpty()) {
            throw new SnapshotException("There are no findBy methods registered with " + snapshotInterfaceType.getName());
        }

        try {
            final List<Future<Indexer<P>>> results = executor.invokeAll(tasks);
            for(Future<Indexer<P>> result: results) {
                try {
                    if(result.get() != null) {
                        cache.cacheIndex(result.get());
                    }
                } catch (ExecutionException e) {
                    logger.error("Exception during indexing, continuing with other index requests ", e);
                    throw new SnapshotException(e);
                }
            }
        } catch (InterruptedException e) {
            throw new SnapshotException(e);
        } finally {
            executor.shutdown();
        }
    }
}
