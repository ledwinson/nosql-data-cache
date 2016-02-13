/**
 * 
 */
package le.cache.bis.services.impl;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import le.cache.bis.services.Cache;
import le.cache.bis.services.CacheService;
import le.cache.bis.services.DataSourceResolver;
import le.cache.bis.services.IndexService;
import le.cache.bis.services.Indexer;
import le.cache.bis.services.ProxyInvocationtarget;
import le.cache.bis.services.Snapshot;
import le.cache.bis.services.exception.SnapshotException;
import le.cache.util.CacheState;

/**
 * @author ledwinson
 *
 */
@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
class CacheServiceImpl<V,S extends Snapshot, P> implements CacheService<V, S, P> {
    
    private static final long WAIT_BEFORE_RETRY = 10;
    private static final int MAX_TRY_COUNT = 50;
    private static final Object[] EMPTY_ARRAY = new Object[]{};
    private final Logger logger = Logger.getLogger(CacheServiceImpl.class.getName());
    
    private CacheState status = CacheState.UNKNOWN;
    private final Lock cacheLock = new ReentrantLock();
    private final Condition snapshotCondition = cacheLock.newCondition();
    private Snapshot snapshot = null;
    private Class<S> snapshotType;
    
    @Autowired
    private Cache<P, V> cache;
    
    @Autowired
    private IndexService<P, V, S> indexService;
    
    @Autowired
    private ProxyInvocationtarget<P,V> proxyInvocationTarget;
       
    @Override
    public void cache(DataSourceResolver<V> dataSource, boolean isCaseSensitive, PrimaryKeyType<P> type, Class<S> snapshotInterfaceType) {        
        try{
            status = CacheState.UNKNOWN;
            
            if(cache.isInvalidated()) {
                throw new SnapshotException("Invalid cache. Cache is invalidated already.");
            }

            this.snapshotType = snapshotInterfaceType;

            cacheLock.lock();
            final Collection<V> data = dataSource.load();
            cache.cache(data, type);

            //create index on cache parallel.
            indexService.index(cache, isCaseSensitive, type, snapshotInterfaceType);

            this.status = CacheState.COMPLETED;

        }catch(Exception e) {
            cache.release();
            logger.error("Exception Occured while caching elements ",e);
            this.status = CacheState.FAILED;
            throw new SnapshotException(e);
        } finally {
            snapshotCondition.signal();
            cacheLock.unlock();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    public void cache(V data, boolean isCaseSensitive, PrimaryKeyType<P> type, Class<S> snapshotInterfaceType) {        
        try{
            status = CacheState.UNKNOWN;
            
            if(cache.isInvalidated()) {
                throw new SnapshotException("Invalid cache. Cache is invalidated already.");
            }

            this.snapshotType = snapshotInterfaceType;

            cacheLock.lock();
            
            final boolean veryFirstElement = cache.getData().isEmpty();// brand new element we need to initiate all the indexers and populate their meta data details.
            final boolean isUpdate = cache.cache(data, type) != null; //if an element is updated we need to re index the entire data set.

            if(veryFirstElement || isUpdate) {
                //create index on cache parallel for the entire cache.
                indexService.index(cache, isCaseSensitive, type, snapshotInterfaceType);
            } else {
                // means it is existing cache with all indexers already populated with their meta-data information. 
                for(Indexer<P> indexer : cache.getAllIndexers()) {
                    indexer.indexElementToTheExistingIndexer((P)type.getMethod().invoke(data, EMPTY_ARRAY), data);
                }
            }

            this.status = CacheState.COMPLETED;

        }catch(Exception e) {
            cache.release();
            logger.error("Exception Occured while caching elements ",e);
            this.status = CacheState.FAILED;
            throw new SnapshotException(e);
        } finally {
            snapshotCondition.signal();
            cacheLock.unlock();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void removeElement(V data, PrimaryKeyType<P> type) {


        try{
            
            status = CacheState.UNKNOWN;
            
            if(cache.isInvalidated()) {
                throw new SnapshotException("Invalid cache. Cache is invalidated already.");
            }

            cacheLock.lock();
            
            if(cache.getData().isEmpty()) {
                return;
            }
            
            final P primryKey = (P)type.getMethod().invoke(data, EMPTY_ARRAY);
            
            if(primryKey == null) {
                return;
            }
            
            final V cachedElement = cache.remove(primryKey);
            
            if(cachedElement == null) {
                return;
            }

            for(Indexer<P> indexer : cache.getAllIndexers()) {
                indexer.removeElementIfPresent(primryKey, data);
            }
            
            this.status = CacheState.COMPLETED;

        }catch(Exception e) {
            cache.release();
            logger.error("Exception Occured while caching elements ",e);
            this.status = CacheState.FAILED;
            throw new SnapshotException(e);
        } finally {
            snapshotCondition.signal();
            cacheLock.unlock();
        }    
    }
    
    
    @Override
    public S getSnapshot() {
        if(cache.isInvalidated()) {
            throw new SnapshotException("This cache is been released already");
        }

        try{
            cacheLock.lock();            
            int awaitCount = 0;
            while(getStatus().isInProgress() || getStatus().isNotSure()) {
                try {
                    awaitCount++;                    
                    if(awaitCount >= MAX_TRY_COUNT) {
                        throw new SnapshotException("Giving up after awaing for Cache to get initialised. Have you ever called the cache method on me? ");
                    }
                    logger.warn("The cache seems not initialized, will wait for cache to load. Retry Count: " + awaitCount);
                    snapshotCondition.await(WAIT_BEFORE_RETRY, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    logger.error("Exception occured while waiting for cache to complete checking cache status again.");
                }
            }
            
            if(getStatus().isFailed()) {
                throw new SnapshotException("Failed Caching data or Cache is corrupted while adding/updating element.");
            }
            
            if(snapshot != null) {
                // here it means the snapshot is already created and ready.
                return this.snapshotType.cast(snapshot);
            }
            
            // we will return the proxy even if the cache loading is failed.
            //The proxy will then return null or empty for all queries.
            proxyInvocationTarget.registerCache(cache);
            
            final Object proxy = Proxy.newProxyInstance(snapshotType.getClassLoader(), new Class[] { snapshotType }, proxyInvocationTarget);
            this.snapshot = (Snapshot) proxy;
            return this.snapshotType.cast(snapshot);
            
        }finally {
            cacheLock.unlock();
        }
    }

    @Override
    public CacheState getStatus() {
        return this.status;
    }

}
