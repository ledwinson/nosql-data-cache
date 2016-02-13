/**
 * 
 */
package le.cache.bis.services.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import le.cache.bis.services.QueryParamResolver;
import le.cache.bis.services.exception.SnapshotException;
import le.cache.util.Property;
import le.cache.util.ReflectionUtil;

/**
 * @author ledwinson
 *
 */
@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
class QueryParamResolverImpl implements QueryParamResolver {

    private final Map<Property, Method[]> propertyResolverMap = new HashMap<>(5);
    
    private boolean metaDataPopulated = false;
    
    //keep in lenear order
    private final List<Property> propertiesInOrderList = new ArrayList<>(5);

    @Override
    public Map<Property, Method[]> getPropertyResolvers() {
        return propertyResolverMap;
    }

    @Override
    public Method[] getPropertyResolver(Property property) {
        return this.propertyResolverMap.get(property);
    }
    
    @Override
    public Method[] addPropertyResolver(Property property, Method[] method) {
        propertiesInOrderList.add(property);
        return this.propertyResolverMap.put(property, method);
    }
    
    @Override
    public List<Property> getPropertiesInOrder() {        
        return propertiesInOrderList;
    }
    
    @Override
    public void populateMetaData(List<Property> properties, Class<?> rootEntityClass) {
        metaDataPopulated = true;
        final List<Method> methodList = new ArrayList<Method>(5);
        for(final Property property: properties) {
            if(property.on() != null && !property.on().isEmpty()) {
                //means the value we are looking for is not directly available in the root object.
                //so we need traverse through the entity hierarchy like getEmployee().getMember().getAge(); 
                final String[] propertyChain = property.on().split("\\.");
                for(String chain: propertyChain) {
                    final Method method = ReflectionUtil.getGetter(chain, rootEntityClass);
                    if(method == null) {
                        throw new SnapshotException("Could not index based on property " + chain + " No getter method exists on " + rootEntityClass.getName());
                    }
                    methodList.add(method);
                    rootEntityClass = method.getReturnType();
                }
            }

            //finally resolve the real property 
            final Method method = ReflectionUtil.getGetter(property.value(), rootEntityClass);
            if(method == null) {
                throw new SnapshotException("Could not index based on property " + property.value() + " No getter method exists on " + rootEntityClass.getName());
            }
            methodList.add(method);
            if(addPropertyResolver(property, methodList.toArray(new Method[]{})) != null) {
                throw new SnapshotException("Ïnvalid Property " + property.value() + " repeated in the same method");
            }
            methodList.clear();
        }
    }
    
    @Override
    public boolean isMetaDataPopulated() {        
        return metaDataPopulated;
    }
}
