package id.co.sigma.zk.ui.controller.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.custom.component.ListOfValueItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.impl.InputElement;

/**
 * base class untuk panel yang memiliki listbox
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseHaveListboxController<DATA> extends BaseSimpleController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1908186628421108883L;
	
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(BaseHaveListboxController.class); 
	
	/**
	 * akses ke service
	 */
	@Autowired
	IGeneralPurposeService generalPurposeService ;  
	
	
	
	
	/**
	 * data model
	 */
	SimpleQueryDrivenListModel<DATA> dataModel ;
	
	
	
	
	DATA selectedItem ; 
	
	
	
	/**
	 * class yang di render controller ini
	 */
	protected abstract Class<? extends DATA> getHandledClass() ;
	
	
	public void invokeSearch () {
		final SimpleQueryFilter[] filters = generateFilters() ;  
		SimpleSortArgument [] sorts = getSorts(); 
		invokeSearch(filters, sorts);
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	public void invokeSearch (final SimpleQueryFilter[] filters ,final   SimpleSortArgument[] sorts) {
		final Class<DATA> dt = (Class<DATA>) getHandledClass();  
		dataModel  = new SimpleQueryDrivenListModel<DATA>() {
			@Override
			public Class<? extends DATA> getHandledClass() {
				return dt;
			}
			@Override
			protected SimpleQueryFilter[] getFilters() {
				return filters;
			}
			@Override
			protected SimpleSortArgument[] getSorts() {
				return sorts;
			}
		};
		Listbox lb =getListbox(); 
		dataModel.initiate(lb.getPageSize());
		lb.setModel(dataModel);
		
	}
	/**
	 * generate filters. ini di lakukan dengan reflection. override ini kalau anda memerlukan query yang berbeda
	 */
	protected SimpleQueryFilter[] generateFilters () {
		Field[] flds =  this.getClass().getDeclaredFields();
		ArrayList<SimpleQueryFilter> flts = new ArrayList<SimpleQueryFilter>()  ; 
		for ( Field scn : flds){
			if ( !scn.isAnnotationPresent(QueryParameterEntry.class))
				continue ; 
			try {
				SimpleQueryFilter f = generateFilter(scn);
				if ( f== null)
					continue ;
				flts.add(f);
			} catch (Exception e) {
				logger.error("gagal membaca parameter query . error : " + e.getMessage() , e);
				continue; 
			}
			 
		}
		if ( flts.isEmpty())
			return null ; 
		SimpleQueryFilter[] retval = new SimpleQueryFilter[flts.size()]; 
		flts.toArray(retval);
		return retval ;
	}
	protected void resetFilter() {
		Field[] flds =  this.getClass().getDeclaredFields();
		for ( Field scn : flds){
			if ( !scn.isAnnotationPresent(QueryParameterEntry.class))
				continue ; 
			try {
				scn.setAccessible(true);
				Object ctrl =  scn.get(this);
				if(ctrl instanceof InputElement) {
					((InputElement)ctrl).setRawValue(null);
				}
			} catch (Exception e) {
				logger.error(e.getMessage() , e);
				continue; 
			}
			 
		}
	}
	
	public final void searchData() {
		invokeSearch();
	}
	
	public final  void resetSearchFilter() {
		resetFilter();
	}
	
	
	protected SimpleQueryFilter generateFilter(Field annotatedField ) throws Exception{
		QueryParameterEntry ann =  annotatedField.getAnnotation(QueryParameterEntry.class);
		SimpleQueryFilterOperator opr =  ann.queryOperator();
		SimpleQueryFilter flt =new SimpleQueryFilter(); 
		flt.setField(ann.filteredField());
		annotatedField.setAccessible(true);
		Object ctrl =  annotatedField.get(this);
		
		InputElement elem = (InputElement) ctrl; 
		Object raw=null;
		
		if(elem instanceof Combobox){
			Combobox cmb = (Combobox)elem;
			int idx = cmb.getSelectedIndex();
			if(idx!=-1){
				raw = cmb.getItemAtIndex(idx).getValue();
			} else {
				raw = cmb.getValue();
			}
		}else{
			raw = elem.getRawValue();
		}
		
		if (raw instanceof String) {
			
			try {			
				Constructor<?>[] c = ann.fieldType().getConstructors();
				if(c.length > 0) {
					flt.setFilterTypeClass(c[0].getName());
				}
			} catch(Exception e) {}
			
			String rawString = (String) raw ; 
			if (  ( rawString == null || rawString.isEmpty()) && ann.skipFilterIfEmpty() ){
				return null ; 
			}
		}
		else   {
			if ( raw == null && ann.skipFilterIfEmpty())
				return null ; 
			if(raw instanceof ListOfValueItem) {
				raw = ((ListOfValueItem)raw).getValue();
			}
		}
		//FIXME: untuk yang memakai in belum siap
		flt.assignFilterWorker(raw);
		
		flt.setOperator(opr);
		return flt; 
	}
	/**
	 * sort argument
	 */
	public SimpleSortArgument[] getSorts() {
		return null ; 
	}

	
	public abstract Listbox getListbox()  ; 
}
