package le.cache.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author ledwinson
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Property {
    String value();
    
    /**
     * 
     * @return the joined property from the root class from which the value can be obtained.
     * <br/> For example the cached entity is Employee and your property is in Member class that is associated with Employee <br/>
     *  then pass the on = "member" along with the property name. The system will then do a employee.getMember().getProperty() ...
     * <br/>
     *  in this case you will pass employee.member as 'on' value.
     */
    String on() default "";
}
