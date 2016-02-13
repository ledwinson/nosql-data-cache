/**
 * 
 */
package le.cache.bis.services.impl.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import le.cache.bis.services.DataSourceResolver;

/**
 * @author ledwinson
 *
 */
public class SampleDataSource implements DataSourceResolver<Membership>{

    private final int size;
    
    public SampleDataSource(int size) {
        this.size = size;
    }
        
    @Override
    public Collection<Membership> load() {
        return getTestData();
    }
    
    private Collection<Membership> getTestData() {
        final List<Membership> members = new ArrayList<>();
        for(int i = 1; i < size +1; i++) {
            members.add(getMember(i));
        }
        return members;
    }
    
    
    public static Membership getMember(int id) {
        return new Membership(Long.valueOf(id), "MemNumber" +id,  String.valueOf((1 + id)));
    }    

}
