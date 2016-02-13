/**
 * 
 */
package le.cache.bis.services.impl.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import le.cache.bis.services.DataSourceResolver;

/**
 * @author ledwinson
 *
 */
public class SampleDataSourceWithLinkedEntities implements DataSourceResolver<Membership>{
    
	
    @Override
    public Collection<Membership> load() {
        return getTestData();
    }
    
    private Collection<Membership> getTestData() {
        final List<Membership> members = new ArrayList<>();
        members.add(getMember(1, new Date()));        
        members.add(getMember(2, null));

        return members;
    }
    
    
    public static Membership getMember(int id, Date dob) {
        final Membership member = new Membership(Long.valueOf(id), "MemNumber" +id,  String.valueOf((1 + id)));
        if(dob != null) {
            final Employee employee = new Employee();
            employee.setDob(dob);
            member.setEmployee(employee);
        }
        return member;
    }    

}
