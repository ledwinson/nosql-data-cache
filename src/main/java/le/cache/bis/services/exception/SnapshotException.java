package le.cache.bis.services.exception;

public class SnapshotException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SnapshotException() {
        super();        
    }

    public SnapshotException(String arg0, Throwable arg1, boolean arg2,boolean arg3) {
        super(arg0, arg1, arg2, arg3);
        
    }

    public SnapshotException(String arg0, Throwable arg1) {
        super(arg0, arg1);        
    }

    public SnapshotException(String arg0) {
        super(arg0);        
    }

    public SnapshotException(Throwable arg0) {
        super(arg0);        
    }
    
    

}
