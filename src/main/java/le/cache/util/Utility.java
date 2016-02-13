/**
 * 
 */
package le.cache.util;

import java.util.Date;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

/**
 * @author ledwinson
 *
 */
public final class Utility {

    private Utility() {        
    }    
    
    public static <T> boolean isAnyNull(T[] t) {
        if(t == null) {
            return true;
        }

        for(T element: t) {
            if(element == null) {
                return true;
            }
        }

        return false;
    }

    public static String getStringValue(Object value, Class<?> type, boolean isCaseSensitive) {
        if(value == null) {
            return "";
        }
        
        /*if(isDateType(type)) {
            final Moment moment = new Moment((Date)value);
            return Location.NEW_SOUTH_WALES.displayLocalDateOnly(moment);
        }*/
        
        if(!isCaseSensitive) {
            return value.toString().toLowerCase();
        }
        return value.toString();
    }

    public static boolean isDateType(Class<?> type) {
        return type.getName().equals(Date.class.getName());
    }
    
    
    public static HashCode guavaHashCode(Object... arguments) {
        if(arguments == null) {
            return HashCode.fromInt(0);
        }

        final HashFunction hf = Hashing.md5();
        final Hasher h = hf.newHasher();
        
        for(Object value: arguments) {
            if(value instanceof String) {
                h.putString(String.class.cast(value), Charsets.UTF_8);
            } else if(value instanceof Boolean) {
                h.putBoolean(Boolean.class.cast(value));
            } else if(value instanceof Double) {
                h.putDouble(Double.class.cast(value));
            } else if(value instanceof Float) {
                h.putFloat(Float.class.cast(value));
            } else if(value instanceof Integer) {
                h.putInt(Integer.class.cast(value));
            } else if(value instanceof Short) {
                h.putShort(Short.class.cast(value));
            } else if(value instanceof Long) {
                h.putLong(Long.class.cast(value));
            } else {
                h.putString(value.toString(), Charsets.UTF_8);
            }
        }
        
        return h.hash();
    }
    
    
}
