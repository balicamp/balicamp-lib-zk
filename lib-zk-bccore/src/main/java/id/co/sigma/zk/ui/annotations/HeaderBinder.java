package id.co.sigma.zk.ui.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderBinder {
	
	/**
	 * header id di UI
	 * @return
	 */
	public String headerId();
	
	/**
	 * target field pojo
	 * @return
	 */
	public String targetField();
	
}
