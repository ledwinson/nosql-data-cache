package le.cache.bis.services;

import java.lang.reflect.InvocationHandler;

/**
 * 
 * @author ledwinson
 *
 */
public interface ProxyInvocationtarget<K,V> extends InvocationHandler {

    void registerCache(Cache<K, V> cache);
}
