package id.co.sigma.zk.ui.controller.base;

import java.lang.reflect.Field;
import java.util.ArrayList;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.InputElement;

/**
 *  
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleListController<DATA> extends BaseSimpleController{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1981062461544583336L;


	
	private Logger logger = LoggerFactory.getLogger(BaseSimpleListController.class);

	
	
	
	 
	
	
	
	SimpleQueryDrivenListModel<DATA> dataModel ;
	
	
	
	
	
	
	
	 
	
	
	/**
	 * class yang di render controller ini
	 */
	protected abstract Class<? extends DATA> getHandledClass() ;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}
	
	public void invokeSearch () {
		final SimpleQueryFilter[] filters = generateFilters() ;  
		SimpleSortArgument [] sorts = getSorts(); 
		invokeSearch(filters, sorts);
	}
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
	
	
	
	protected SimpleQueryFilter generateFilter(Field annotatedField ) throws Exception{
		QueryParameterEntry ann =  annotatedField.getAnnotation(QueryParameterEntry.class);
		SimpleQueryFilterOperator opr =  ann.queryOperator();
		SimpleQueryFilter flt =new SimpleQueryFilter(); 
		flt.setField(ann.filteredField());
		annotatedField.setAccessible(true);
		Object ctrl =  annotatedField.get(this);
		
		
		InputElement elem = (InputElement) ctrl; 
		
		Object raw = elem.getRawValue(); 
		if (raw instanceof String) {
			String rawString = (String) raw ; 
			if (  ( rawString == null || rawString.isEmpty()) && ann.skipFilterIfEmpty() ){
				return null ; 
			}
		}
		else   {
			if ( raw == null && ann.skipFilterIfEmpty())
				return null ; 
		}
		//FIXME: untuk yang memakai in belum siap
		flt.assignFilterWorker(elem.getRawValue());
		
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
