/**
 * 
 */
package le.cache.bis.services.index;

import java.util.Date;

import le.cache.bis.services.Snapshot;
import le.cache.bis.services.impl.data.Membership;
import le.cache.util.Property;

/**
 * @author ledwinson
 *
 */
public interface SigleMethodJoinPropertySnapShot extends Snapshot {

    Membership findByMemberNumberAndTfnAndDob(@Property("memberNumber") String memberNumber, @Property(value = "dob", on = "employee") Date dob);
}
