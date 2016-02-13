package le.cache.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

public final class ReflectionUtil {

    private ReflectionUtil() {       
    }

    public static Field getParam(String parameterName, Class<?> clazz) {
        return ReflectionUtils.findField(clazz, parameterName);
    }
    
    public static Method getGetter(String parameterName, Class<?> clazz) {
        final char[] array = parameterName.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        return ReflectionUtils.findMethod(clazz, "get" + String.valueOf(array));
    }
    
    public static Method[] getAllMethods(Class<?> clazz) {
        return ReflectionUtils.getUniqueDeclaredMethods(clazz);
    }
}
