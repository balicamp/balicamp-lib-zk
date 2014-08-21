package id.co.sigma.zk.ui.annotations;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * filter untuk menganotasi pada parameter nya langsung. ini untuk di taruh dalam k
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface QueryParameterEntry {
	
	
	
	/**
	 * field yang di filter filter. ini adalah JPA field atau hibernate mapped field
	 */
	public String filteredField() ; 
	
	
	/**
	 * query operator untuk field. operator untuk query apa saja
	 */
	public SimpleQueryFilterOperator queryOperator () default SimpleQueryFilterOperator.likeTailOnly;
	
	
	
	
	/**
	 * skip filter kalau misal isian kosong. 
	 * default = true
	 */
	public boolean skipFilterIfEmpty() default true;  
	

}
