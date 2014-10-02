package id.co.sigma.zk.ui.data;

import id.co.sigma.zk.ui.annotations.ControlDataBinder;
import id.co.sigma.zk.ui.component.IBindableComponent;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;



/**
 * class helper untuk membind data data dari form ke object 
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public final class FormDataBinderUtil {
	
	
	
	
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(FormDataBinderUtil.class.getName());
	
	
	
	private FormDataBinderUtil() {}
	
	
	private static FormDataBinderUtil instance ; 
	
	/**
	 * cache param binder
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, IFormDataBinder> cachedBinders = new HashMap<String, IFormDataBinder>(); 
	
	
	@SuppressWarnings(value={"rawtypes" , "unchecked"})
	public void bindDataFromControl (Object targetForBind , Object ownerOfControls  ) throws Exception{
		Field[] allFields =  ownerOfControls.getClass().getDeclaredFields();
		for ( Field scn : allFields) {
			if ( !scn.isAnnotationPresent(ControlDataBinder.class))
				continue ; 
			ControlDataBinder ann = scn.getAnnotation(ControlDataBinder.class);
			if (! scn.isAccessible())
				scn.setAccessible(true);
			Object ctrl = scn.get(ownerOfControls); 
			
			
			Class binderCls =  ann.dataBinderClass();
			String fld =  ann.targetField();
			String key = binderCls.getName() ; 
			if ( !cachedBinders.containsKey(key)){
				
				IFormDataBinder f =  (IFormDataBinder)BeanUtils.instantiate(binderCls);
				cachedBinders.put(key, f); 
			}
			cachedBinders.get(key).bindDataFromControl(ctrl, targetForBind, fld);
		}
	}
	
	
	/**
	 * bind value dari control yang di extends
	 */
	public void bindDataFromControl (Object targetForBind , IdSpace idSpace )  throws Exception{
		Collection<Component> allComps =  idSpace.getFellows();
		for ( Component scn : allComps ) {
			if ( !(scn instanceof IBindableComponent))
				continue ; 
			IBindableComponent b = (IBindableComponent)scn ;
			String key = b.getBinderClassFQCN() ; 
			if ( key== null || key.isEmpty())
				key = DefaultFormDataBinder.class.getName(); 
			if ( !cachedBinders.containsKey(key)){
				IFormDataBinder f =  (IFormDataBinder) BeanUtils.instantiate(Class.forName(key));
				cachedBinders.put(key, f); 
			}
			cachedBinders.get(key).bindDataFromControl(scn, targetForBind, b.getTargetBindField());
		}
	}
	
	public static FormDataBinderUtil getInstance() {
		if ( instance == null)
			instance = new FormDataBinderUtil(); 
		return instance;
	}
}
