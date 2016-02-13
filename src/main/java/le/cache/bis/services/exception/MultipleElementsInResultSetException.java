/**
 * 
 */
package le.cache.bis.services.exception;

/**
 * @author ledwinson
 *
 */
public class MultipleElementsInResultSetException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MultipleElementsInResultSetException() {
        super();
    }

    public MultipleElementsInResultSetException(String message,
            Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MultipleElementsInResultSetException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleElementsInResultSetException(String message) {
        super(message);
    }

    public MultipleElementsInResultSetException(Throwable cause) {
        super(cause);
    }

}
