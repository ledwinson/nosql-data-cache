/**
 * 
 */
package le.cache.util;

/**
 * @author ledwinson
 *
 */
public enum CacheState {

    INPROGRESS, FAILED, COMPLETED, UNKNOWN;
    
    public boolean isInProgress() {
        return this == INPROGRESS;
    }
    
    public boolean isCompleted() {
        return this == COMPLETED;
    }
    
    public boolean isFailed() {
        return this == FAILED;
    }
    
    public boolean isNotSure() {
        return this == UNKNOWN;
    }
}
