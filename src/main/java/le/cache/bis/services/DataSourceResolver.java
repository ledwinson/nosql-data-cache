/**
 * 
 */
package le.cache.bis.services;

import java.util.Collection;

/**
 * @author ledwinson
 *
 * <br/>
 * 
 * This interface need to be submitted to the cache service to create the snapshot of the data.
 * <br/>
 * This interface can be implemented for different data sources and can be injected to cache service. 
 */
public interface DataSourceResolver<T> {

    Collection<T> load();
}
