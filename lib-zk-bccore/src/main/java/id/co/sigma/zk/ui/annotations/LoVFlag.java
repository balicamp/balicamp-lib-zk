package id.co.sigma.zk.ui.annotations;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;

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
	String value() default "";
	
	/**
	 * data type
	 * @return
	 */
	Class<?> type() default String.class;
	
	/**
	 * filter operator
	 * @return
	 */
	SimpleQueryFilterOperator operator() default SimpleQueryFilterOperator.equal;
}
