/**
 * 
 */
package le.cache.bis.services;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import le.cache.util.Property;

/**
 * All the methods starts with "finaBy" in the {@link Snapshot} interface type must have their parameters marked with @Property annotation.
 * <br/>
 * This will help us to navigate to child objects and evaluate the property value to index.
 * <br/>
 * We will keep all the properties that belongs to a particular index here.
 * <br/>
 * This is a meta data for index information used to index data.
 * 
 * @author ledwinson
 *
 *
 */
public interface QueryParamResolver {

    /**
     * 
     * @return the property resolvers for this index. <br/> Will never returns null.
     */
    Map<Property, Method[]> getPropertyResolvers();

    /**
     * 
     * @param property the property to look the resolver for.
     * @return The method array to resolve the property if found or null.
     */
    Method[] getPropertyResolver(Property property);

    /**
     * 
     * @param property the property to add to meta data. 
     * @param method the method to resolve the property.
     * @return the previous registered methods if any for the given property otherwise null.
     */
    Method[] addPropertyResolver(Property property, Method[] method);
    
    /**
     * 
     * @param properties the properties to resolve
     * @param rootEntityClass the root entity cached and the sub elements can be found.
     */
    void populateMetaData(List<Property> properties, Class<?> rootEntityClass);
    
    /**
     * @return the properties as in the order they added.
     */
    List<Property> getPropertiesInOrder();
    
    boolean isMetaDataPopulated();
}
