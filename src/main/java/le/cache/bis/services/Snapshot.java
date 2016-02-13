/**
 * 
 */
package le.cache.bis.services;



/**
 * @author ledwinson
 * 
 * <br/>
 * A snapshot will be created with the values loaded from the given data source after indexing the data based on the 
 * queries provided in an interface extended from this interface.  
 * <h1><font size = 3>Follow the rules below with your interface for this Snapshot type.</font></h1>
 *  <ol>
 *  <li><b><strong><font size = 2>The method names <b>must starts with <i>findBy</i></b> and pass @Property annotation before each parameter.</font></strong></b></li>
 *  <li><b><strong><font size = 2>Do not over load same finder methods. that is use different names for each finder method.</font></strong></b></li>
 *  <li><b><strong><font size = 2>If you don't expect multiple results use return type as your entity other wise use a List or Set.</font></strong></b></li>
 *  <li><b><strong><font size = 2>If you have a return type that expect one object and if the result set has more than one object it will throw a <br/>
 *  </font></strong></b> {@link le.cache.bis.services.exception.SnapshotException}</li>
 *  </ol>
 * <h1>Example</h1> 
 * In the below query the user is looking for a Membership in cache with member number and date of birth fields. <b>Note that dob field is in the employee object of the Membership Object </b>.
 * <br/>
 * <code><i>Membership findByMemberNumberAndTfnAndDob(<b>@Property("memberNumber")</b> String memberNumber, @Property(value = "dob", <b>on = "employee"</b>) Date dob);</i></code>
 * <br/> 
 */
public interface Snapshot {
    
    String METHOD_PREFIX = "findBy";
    
    /**
     * Call this method to clear the cache after you have done with your work.
     * <br/>
     * If you havn't called this method, the cache is never released, so call this method always.
     * <br/>
     * <b>Not to developers : If you change this method name for any reason, change it in the proxy target class as well.</b>  
     */
    void release();
    
    /**
     * 
     * @return the size of the snapshot
     */
    Integer size();
}
