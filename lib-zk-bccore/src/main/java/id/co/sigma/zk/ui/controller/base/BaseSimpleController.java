package id.co.sigma.zk.ui.controller.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.sigma.common.data.CustomDataFormatter;
import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.common.data.lov.CommonLOVHeader;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.lov.ILOVProviderService;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.lov.CommonLOVWithRenderer;
import id.co.sigma.zk.ui.lov.DefaultLOVRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

/**
 * base class untuk ZK MVC controller 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleController extends SelectorComposer<Component>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7154919543754238692L;
	
	private boolean springWired = false ; 
	
	
	private static final Logger logger = LoggerFactory.getLogger(BaseSimpleController.class); 
	
	private Map<Class<?>, CustomDataFormatter<CommonLOV>> indexedCustomRender = new HashMap<Class<?>, CustomDataFormatter<CommonLOV>>() ;
	
	private DefaultLOVRenderer defaultLOVRenderer = new DefaultLOVRenderer();
	
	
	@Autowired
	ILOVProviderService lovProviderService;
	
	
	
	/**
	 * general purpose dao. untuk akses ke database yang tidak perlu spesifik
	 */
	@Autowired
	protected IGeneralPurposeDao generalPurposeDao ; 

	public BaseSimpleController() {
		super() ; 
		
	}
	
	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		
		super.doBeforeComposeChildren(comp);
		wireSpring();
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		fillLOVControls();
	}
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		
		ComponentInfo retval =  super.doBeforeCompose(page, parent, compInfo);
		wireSpring();
		return retval ; 
	}
	
	private void wireSpring () {
		if ( springWired)
			return ; 
		try {
			SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
			springWired = true ; 
		} catch (Exception e) {
			springWired = false ;
		}
	}
	
	
	/**
	 * language dari current user
	 */
	public String getLanguge() {
		return "id"; 
	}
	
	/**
	 * mengisi semua control dengan annotation {@link LookupEnabledControl}
	 */
	protected void fillLOVControls () {
		Field[] flds =  this.getClass().getDeclaredFields();
		 
		Map<String, List<Field>> indexedParamField = new HashMap<String, List<Field>>();
		
		Map<Field, Class<?> > customRenderer = new HashMap<Field, Class<?>>() ; 
		for ( Field scn : flds){
			if ( !scn.isAnnotationPresent(LookupEnabledControl.class))
				continue ;
			LookupEnabledControl ann = scn.getAnnotation(LookupEnabledControl.class); 
			String paramId =  ann.parameterId();
			if (! DefaultLOVRenderer.class.getName().equals(  ann.lovDataRenderer().getName())){
				customRenderer.put(scn, ann.lovDataRenderer()); 
			}
			if (! indexedParamField.containsKey(paramId))
				indexedParamField.put(paramId, new ArrayList<Field>()); 
			indexedParamField.get(paramId).add(scn); 
		}
		if ( indexedParamField.isEmpty())
			return ; 
		Map<String, CommonLOVHeader> vals =  lovProviderService.getLOVAsMap(getLanguge(), indexedParamField.keySet());
		if ( vals== null)
			return  ; 
		for ( String paramId : vals.keySet()) {
			CommonLOVHeader lov = vals.get(paramId);
			List<Field> fs =  indexedParamField.get(lov.getLovId());
			
			if (! indexedParamField.containsKey(lov.getLovId()) || fs == null ||fs.isEmpty())
				continue ; 
			for ( Field fld : fs ) {
				if (! customRenderer.containsKey(fld)){
					try {
						fillLooukup(fld, lov, this.defaultLOVRenderer);
					} catch (Exception e) {
						logger.error("gagal render combo :" + lov.getLovId() + ",error : " + e.getMessage() , e);
					}
				}
				else {
					Class<?> r = customRenderer.get(fld); 
					if (! indexedCustomRender.containsKey(r)){
						CustomDataFormatter<CommonLOV> rnd = (CustomDataFormatter<CommonLOV>)BeanUtils.instantiate(r); 
						indexedCustomRender.put(r, rnd); 
					}	
					try {
						fillLooukup(fld, lov, indexedCustomRender.get(r));
					} catch (Exception e) {
						logger.error("gagal render combo :" + lov.getLovId() + ",error : " + e.getMessage() , e);
					}
					
				}
			}
			
			
		}
	}
	
	
	
	
	
	/**
	 * render lookup
	 */
	protected void fillLooukup (Field field, CommonLOVHeader header  , CustomDataFormatter<CommonLOV> dataRenderer ) throws Exception{
		field.setAccessible(true);
		Object swp =  field.get(this);
		if ( swp instanceof Combobox) {
			Combobox cmb = (Combobox)swp ; 
			ListModelList<CommonLOVWithRenderer> models = new ListModelList<CommonLOVWithRenderer>(); 
			if ( header.getDetails()!= null ) {
				ArrayList<CommonLOVWithRenderer> contents = new ArrayList<CommonLOVWithRenderer>() ;
				
				for ( CommonLOV scn : header.getDetails() ) {
					CommonLOVWithRenderer w = new CommonLOVWithRenderer(scn, dataRenderer) ; 
					contents.add(w) ; 
					
				}
				models.addAll(contents); 
			}
			cmb.setModel(models);
			
		}
	}
	
}
