package id.co.sigma.zk.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import id.co.sigma.common.data.CustomDataFormatter;
import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.zk.ui.lov.DefaultLOVRenderer;

/**
 * annotation pada level field. untuk field yang LOV enabled
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface LookupEnabledControl {

	
	
	/**
	 * ID parameter. ini refer ke isi dari table m_lookup_header atau ID custom lookup
	 */
	String parameterId () ; 
	
	
	
	/**
	 * render class. yang menggerate data untuk key vs value
	 */
	Class<? extends CustomDataFormatter<CommonLOV>> lovDataRenderer () default DefaultLOVRenderer.class ; 
}
