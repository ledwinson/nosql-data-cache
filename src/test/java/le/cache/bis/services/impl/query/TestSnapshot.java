/**
 * 
 */
package le.cache.bis.services.impl.query;

import java.util.Date;
import java.util.List;

import le.cache.bis.services.Snapshot;
import le.cache.bis.services.impl.data.Membership;
import le.cache.util.Property;


/**
 * @author ledwinson
 *
 */
public interface TestSnapshot extends Snapshot {

    Membership findByMemberNumberAndTfnAndDob(@Property("memberNumber") String memberNumber, 
            @Property("superFundGenEmprId") String tfn, @Property(value = "dob", on = "employee") Date dob);

    Membership findByMemberNumberAndTfnForEmployer(@Property("memberNumber") String memberNumber,  @Property("superFundGenEmprId") String tfn);
    
    List<Membership> findByMemberNumberAndTfnForEmployerForDuplicates(@Property("memberNumber") String memberNumber,  @Property("superFundGenEmprId") String tfn);
}
