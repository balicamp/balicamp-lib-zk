package id.co.sigma.zk.ui.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoVSort {

	String field();
	
	boolean ascending() default true;
	
}
