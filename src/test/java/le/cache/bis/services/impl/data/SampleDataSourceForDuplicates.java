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
public class SampleDataSourceForDuplicates implements DataSourceResolver<Membership>{

    private final int size;
    
    public SampleDataSourceForDuplicates(int size) {
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
        return new Membership(Long.valueOf(id), "MemNumber" +1,  String.valueOf((2)));
    }
}
