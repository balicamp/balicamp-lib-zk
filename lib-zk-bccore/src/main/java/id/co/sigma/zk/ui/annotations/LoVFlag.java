package id.co.sigma.zk.ui.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoVFlag {

	/**
	 * field name
	 * @return
	 */
	String field();
	
	/**
	 * flag value
	 * @return
	 */
	String value();
	
	/**
	 * data type
	 * @return
	 */
	Class<?> type() default String.class;
}
