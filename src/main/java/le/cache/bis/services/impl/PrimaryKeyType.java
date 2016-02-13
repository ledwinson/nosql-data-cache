/**
 * 
 */
package le.cache.bis.services.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import le.cache.bis.services.exception.SnapshotException;
import le.cache.util.ReflectionUtil;

/**
 * @author ledwinson
 *
 */
public final class PrimaryKeyType<T> {

    private Class<T> type;
    private String uniqueKeyParameterName;
    private Field field;
    private Method method;
    private Class<?> entityClass;

    public PrimaryKeyType(Class<T> type, String uniqueKeyParameterName, Class<?> entityClass) {
        this.type = type;
        this.uniqueKeyParameterName = uniqueKeyParameterName;

        if(type == null || uniqueKeyParameterName == null) {
            throw new RuntimeException("Invalid Primary Key variables.");
        }
        
        this.entityClass = entityClass;
        if(entityClass == null) {
            throw new SnapshotException("Invalid Entity Class " + entityClass);
        }

        this.field = ReflectionUtil.getParam(uniqueKeyParameterName, entityClass);    
        if(field == null) {
            throw new SnapshotException("Invalid primary key " + type);
        }               
        
        this.method = ReflectionUtil.getGetter(uniqueKeyParameterName, entityClass);
        if(method == null) {
            throw new SnapshotException("No getter method exists for key " + uniqueKeyParameterName);
        }
    }

    public Class<T> getType() {
        return type;
    }

    public String getUniqueKeyParameterName() {
        return uniqueKeyParameterName;
    }

    @Override
    public String toString() {
        return getUniqueKeyParameterName() + " : " + getType();
    }

    public Field getField() {
        return field;
    }
    
    public Method getMethod() {
        return method;
    }
    
    public Class<?> getEntityClass() {
        return entityClass;
    }
}
