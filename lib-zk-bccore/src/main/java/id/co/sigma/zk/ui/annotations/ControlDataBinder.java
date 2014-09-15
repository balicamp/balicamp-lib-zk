package id.co.sigma.zk.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * binder data vs control
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface ControlDataBinder {
	
	
	
	/**
	 * field tujuan, kemana data akan di set
	 */
	public String targetField ()  ; 

}
