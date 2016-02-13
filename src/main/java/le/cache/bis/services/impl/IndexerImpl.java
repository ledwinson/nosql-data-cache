/**
 * 
 */
package le.cache.bis.services.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.hash.HashCode;

import le.cache.bis.services.Cache;
import le.cache.bis.services.Indexer;
import le.cache.bis.services.QueryParamResolver;
import le.cache.bis.services.exception.SnapshotException;
import le.cache.util.Property;
import le.cache.util.Utility;

/**
 * @author ledwinson
 *
 */
@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
class IndexerImpl<P> implements Indexer<P> {

    public static final HashCode NO_INDEX_KEY_RESOLVED = HashCode.fromInt(-1);
    
    private final Logger logger = Logger.getLogger(IndexerImpl.class.getName());
    
    private String queryName;
    private PrimaryKeyType<P> keyType;
    private boolean isCaseSensitive = true;
    private List<Property> properties;
    

    private Map<HashCode, List<P>> indexedCache = new HashMap<>();

    @Autowired
    private QueryParamResolver queryParamsSolver;

    @Override
    public <V> void indexBy(List<Property> properties, Cache<P, V> cache) {
        this.properties = properties;

        if(this.properties == null ||  keyType == null || queryName == null) {
            throw new SnapshotException("Please set the required values before caling index method");
        }

        //keep the meta data for this index.
        queryParamsSolver.populateMetaData(properties, keyType.getEntityClass());

        //nothing to index on.
        if(cache.isEmpty()) {
            return;
        }

        final Map<P,V> data = cache.getData();
        final Set<P> caheDatakeySet = data.keySet();

        for(P thePrimaryKeyOfEntity: caheDatakeySet) {//for each element in cache, try to index.
            final Object objectOnWhichTheMethodTobeInvoked = data.get(thePrimaryKeyOfEntity);//the root entity first
            indexElementToTheExistingIndexer(thePrimaryKeyOfEntity, objectOnWhichTheMethodTobeInvoked);
        }
    }
    
    @Override
    public void indexElementToTheExistingIndexer(P thePrimaryKeyOfNewElementInCache, Object objectOnWhichTheMethodTobeInvoked) {        
        if(!this.queryParamsSolver.isMetaDataPopulated()) {
            throw new SnapshotException("The indexer is not correctly initialized to call this method.");
        }
        
        final HashCode indexKey = workoutIndexKeyFromProperties(objectOnWhichTheMethodTobeInvoked);
        if(!indexKey.equals(NO_INDEX_KEY_RESOLVED)) {
            populatecache(indexKey, thePrimaryKeyOfNewElementInCache);
        }
    }
    
    @Override
    public void removeElementIfPresent(P thePrimaryKeyOfElementInCache, Object objectOnWhichTheMethodTobeInvoked) {
        if(!this.queryParamsSolver.isMetaDataPopulated() 
                || thePrimaryKeyOfElementInCache == null 
                || objectOnWhichTheMethodTobeInvoked == null) {
            return;
        }
        
        final HashCode indexKey = workoutIndexKeyFromProperties(objectOnWhichTheMethodTobeInvoked);
        if(!indexKey.equals(NO_INDEX_KEY_RESOLVED)) {
            final List<P> existing = indexedCache.get(indexKey);
            if(existing != null && existing.size() > 0) {
                if(existing.remove(thePrimaryKeyOfElementInCache)) {
                    logger.debug("Removed Element From Index For Key " + thePrimaryKeyOfElementInCache);
                }
                
                if(existing.isEmpty()) {
                    indexedCache.remove(indexKey);
                }
            }
        }
    }
    
    private HashCode workoutIndexKeyFromProperties(Object therootElementFromCacheToIndex) {
        final List<Property> propsKeys = queryParamsSolver.getPropertiesInOrder();
        final List<Object> propertiesList = new ArrayList<>(propsKeys.size());
        final Object[] params = new Object[]{};

        main: for(Property property : propsKeys) {
            Object objectOnWhichTheMethodTobeInvoked = therootElementFromCacheToIndex;
            try {             
                Object thePropertyValue = null;
                Method methodResolvedLastIntheHierarchy = null;
                //there will be at least one method and no need to null check below as the queryParamsSolver guarantees that 
                for(Method methodToResolveProperty: queryParamsSolver.getPropertyResolver(property)) {
                    thePropertyValue = methodToResolveProperty.invoke(objectOnWhichTheMethodTobeInvoked, params);
                    objectOnWhichTheMethodTobeInvoked = thePropertyValue;
                    if(thePropertyValue == null) {
                        propertiesList.clear();
                        break main; // don't index if at least one property is null.
                    }
                    methodResolvedLastIntheHierarchy = methodToResolveProperty;
                }     
                
                if(!isCaseSensitive && methodResolvedLastIntheHierarchy.getReturnType().getName().equals(String.class.getName())) {
                    propertiesList.add(thePropertyValue.toString().toLowerCase());
                    continue main;
                }
                propertiesList.add(thePropertyValue);

            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new SnapshotException(e);
            }
        }

        if(propertiesList.size() > 0) {
            return Utility.guavaHashCode(propertiesList.toArray());
        }
        return NO_INDEX_KEY_RESOLVED;
    }
    
    

    private void populatecache(HashCode indexKey, P value) {
        final List<P> existing = indexedCache.get(indexKey);
        if(existing != null && !existing.contains(value)) {
            //Copy cost but for memory efficiency when using collection List.
            final List<P> list = new ArrayList<>(existing.size() + 1);
            list.addAll(existing);
            list.add(value);
            indexedCache.put(indexKey, list);
        } else {
            final List<P> list = new ArrayList<>(1);
            list.add(value);
            indexedCache.put(indexKey, list);
        }
    }

    @Override
    public Indexer<P> setQuery(String queryName) {
        this.queryName = queryName;
        return this;
    }

    @Override
    public Indexer<P> setPrimaryKeyType(PrimaryKeyType<P> keyType) {
        this.keyType = keyType;
        return this;
    }

    @Override
    public Indexer<P> setCaseSensitiveSearch(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
        return this;
    }

    @Override
    public String getName() {
        return this.queryName;
    }

    @Override
    public List<P> getKeys(String name, Object[] arguments) {

        if(Utility.isAnyNull(arguments)) {
            return new ArrayList<>(1);
        }

        final List<Object> propertiesList = new ArrayList<>(arguments.length);
        for(Object argument : arguments) {
            if(!isCaseSensitive && argument.getClass().getName().equals(String.class.getName())) {
                propertiesList.add(argument.toString().toLowerCase());
                continue;
            }
            propertiesList.add(argument);
        }

        final HashCode indexKey = Utility.guavaHashCode(propertiesList.toArray());
        final List<P> primaryKeysForLookUp = indexedCache.get(indexKey);
        return primaryKeysForLookUp == null ? new ArrayList<P>(1) : primaryKeysForLookUp;
    }

    @Override
    public Property[] getQueryParams() {
        return this.properties.toArray(new Property[]{});
    }

    /*@Override
    public Method[] getMethodNames() {
        return this.methods == null ? new Method[]{} : methods;
    }*/

}
