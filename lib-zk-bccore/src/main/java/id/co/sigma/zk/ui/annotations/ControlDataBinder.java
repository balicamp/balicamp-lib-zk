package id.co.sigma.zk.ui.annotations;

import id.co.sigma.zk.ui.data.DefaultFormDataBinder;
import id.co.sigma.zk.ui.data.IFormDataBinder;

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
	 * field tujuan, kemana data akan di set. ini bisa pakai .[dot] kalau field target nested<br/>
	 * Misal class : 
	 * <code>
	 * public class Department {<br/>
	 * &nbsp;private String name ; <br/>
	 * }<br/><br/>
	 * public class Person {<br/>
	 * &nbsp;private Department dept ;<br/>
	 * }
	 * field bisa di set dengan : <br/>
	 * <i>dept.name</i>untuk mengeset name. syaratnya : field dept tidak null
	 *  
	 *  </code>
	 */
	public String targetField ()  ; 
	
	
	
	
	/**
	 * data binder class
	 */
	@SuppressWarnings("rawtypes")
	public Class<? extends IFormDataBinder> dataBinderClass() default DefaultFormDataBinder.class ; 
	
	
}

