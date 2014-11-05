package id.co.sigma.zk.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListOfValue {

	/**
	 * lov pojo 
	 * @return
	 */
	Class<?> lovClass();
	
	/**
	 * separator antara kode dan keterangan yang membentuk label <br/>
	 * misal: <br/> 
	 * - kode = 001 <br/>
	 * - keterangan = Contoh Label
	 * - separator = " - " <br/>
	 * maka: <br/>
	 * - label  = 001 - Contoh Label
	 * @return
	 */
	String separator() default " - ";
	
	/**
	 * field pojo untuk mengisi value
	 * @return
	 */
	String valueField();
	
	/**
	 * field code jika tidak diisi maka field code = field value
	 * @return
	 */
	String codeField() default "";
	
	/**
	 * field pojo untuk keterangan
	 * @return
	 */
	String labelField();
	
	/**
	 * id dari component LOV (Bandbox atau Combobox)
	 * @return
	 */
	String componentId() default "";
	
	/**
	 * flag load on demand
	 * @return
	 */
	boolean onDemand() default true;
	
	LoVFlag[] filterFlags() default {};
	
	LoVSort[] sorts() default {};
	
	String dependOn() default "";
}
