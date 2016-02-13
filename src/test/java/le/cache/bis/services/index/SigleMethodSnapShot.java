/**
 * 
 */
package le.cache.bis.services.index;

import java.util.List;

import le.cache.bis.services.Snapshot;
import le.cache.bis.services.impl.data.Membership;
import le.cache.util.Property;

/**
 * @author ledwinson
 *
 */
public interface SigleMethodSnapShot extends Snapshot {

    List<Membership> findByMemberNumberAndTfnForEmployer(@Property("memberNumber") String memberNumber,  @Property("superFundGenEmprId") String tfn);
}
