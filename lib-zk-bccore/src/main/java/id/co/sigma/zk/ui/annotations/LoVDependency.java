package id.co.sigma.zk.ui.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoVDependency {

	/**
	 * isian combobox tergantung dari combobox id ini
	 * @return
	 */
	String comboId();
	
	/**
	 * tipe data dari combo
	 * @return
	 */
	Class<?> dataType();
	
	/**
	 * nama field yang berasosiasi dengan data dari combo parent
	 * @return
	 */
	String field();
	
	/**
	 * value dari commbo ini akan difilter juga dari dependency combo
	 * @return
	 */
	boolean isFilter() default true;
	
	/**
	 * perubahan combobox ini akan menghapus item pada dependency combo
	 * @return
	 */
	boolean clearItems() default false;
}
