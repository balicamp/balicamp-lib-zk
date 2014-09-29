package id.co.sigma.zk.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChildGridData {

	/**
	 * entity class
	 * @return
	 */
	public Class<?> entity();
	
	/**
	 * grid id
	 * @return
	 */
	public String gridId();
	
	public HeaderBinder[] headerBinder() default {};
	
	public JoinKey[] joinKeys(); 
}
