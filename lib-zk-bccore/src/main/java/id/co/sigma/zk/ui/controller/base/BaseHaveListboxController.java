package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.custom.component.ListOfValueItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.ListitemRenderer;
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
	
	/**
	 * Default sort direction
	 */
	private boolean sortAscending = true;
	
	private static final Logger logger = LoggerFactory.getLogger(BaseHaveListboxController.class); 
	
	/**
	 * akses ke service
	 */
	@Autowired
	protected IGeneralPurposeService generalPurposeService ;  
	
	
	
	
	/**
	 * data model
	 */
	protected SimpleQueryDrivenListModel<DATA> dataModel ;
	
	
	
	
	protected DATA selectedItem ; 
	
	protected List<DATA> multipleSelectedItem;
	
	public boolean isSortAscending() {
		return sortAscending;
	}
	
	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}
	
	/**
	 * class yang di render controller ini
	 */
	protected abstract Class<? extends DATA> getHandledClass() ;
	
	
	public void invokeSearch () {
		final SimpleQueryFilter[] filters = generateFilters() ;  
		SimpleSortArgument [] sorts = getSorts(); 
		invokeSearch(filters, sorts);
	}
	
	
	public void invokeSearch (final SimpleQueryFilter[] filters ,final   SimpleSortArgument[] sorts) {
		dataModel  = instantiateDataModel(filters, sorts); 
		Listbox lb = getListbox(); 
		dataModel.setSortArgs(getSortableFirstHeader(lb));
		dataModel.initiate(lb.getPageSize());
		dataModel.setMultiple(lb.isMultiple());
		lb.setModel(dataModel);
		if (dataModel != null && dataModel.getHoldedData().isEmpty()) {
			lb.setEmptyMessage(Labels.getLabel("msg.search.empty_result"));
			lb.invalidate();
		}else{
			ListitemRenderer<DATA> renderer = getCustomRenderer(); 
			if ( renderer!= null){
				lb.setItemRenderer(renderer);
			}
		}
	}
	
	
	
	/**
	 * worker untuk instantiate data model untuk data.
	 * override ini kalau ada perlu query yang dedicated atau semacamnya
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	protected SimpleQueryDrivenListModel<DATA> instantiateDataModel (final SimpleQueryFilter[] filters ,final   SimpleSortArgument[] sorts) {
		final Class<DATA> dt = (Class<DATA>) getHandledClass();
		return new SimpleQueryDrivenListModel<DATA>() {
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
	}
	
	
	
	
	/**
	 * renderer Dedicated untuk render column. ini di pergunakan kalu render column di rasa lebih mudah dengan java code. 
	 * silakan cek di sini : <a href="http://books.zkoss.org/wiki/User:Jimmyshiau/Comp_Ref_Listbox#ListitemRenderer">http://books.zkoss.org/wiki/User:Jimmyshiau/Comp_Ref_Listbox#ListitemRenderer</a> 
	 * 
	 * 
	 */
	protected ListitemRenderer<DATA> getCustomRenderer () {
		return null ; 
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
	
	protected final SimpleSortArgument[] getSortableFirstHeader(Listbox lb) {
		SimpleSortArgument[] sorts = null;
		Listhead headers = lb.getListhead();
		List<Listheader> lhdrs = headers.getChildren();
		for(Listheader hdr : lhdrs) {
			if(sortAscending){
				if((hdr.getSortAscending() != null) && (sorts == null)) {
					FieldComparator cmpr = (FieldComparator)hdr.getSortAscending();
					hdr.setSortDirection("ascending");
					sorts = new SimpleSortArgument[] {
						new SimpleSortArgument(cmpr.getRawOrderBy(), true)	
					};
				} else {
					hdr.setSortDirection("natural");
				}
			}else{
				if((hdr.getSortDescending() != null) && (sorts == null)) {
					FieldComparator cmpr = (FieldComparator)hdr.getSortDescending();
					hdr.setSortDirection("descending");
					sorts = new SimpleSortArgument[] {
						new SimpleSortArgument(cmpr.getRawOrderBy(), false)	
					};
				} else {
					hdr.setSortDirection("natural");
				}
			}
		}
		return sorts;
	}
}
