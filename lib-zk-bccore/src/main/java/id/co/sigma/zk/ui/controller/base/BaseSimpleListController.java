package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.impl.InputElement;

/**
 *  
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleListController<DATA extends Serializable> extends BaseSimpleController{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1981062461544583336L;


	
	private Logger logger = LoggerFactory.getLogger(BaseSimpleListController.class);

	
	
	
	 
	@Autowired
	private IGeneralPurposeService generalPurposeService ;  
	
	
	SimpleQueryDrivenListModel<DATA> dataModel ;
	
	
	
	@SuppressWarnings("unused")
	private DATA selectedItem ; 
	
	
	@Autowired
	@Qualifier(value="transactionManager")
	protected PlatformTransactionManager  transactionManager ; 
	
	
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
	
	protected Map<Class<?>, SimpleQueryFilter[]> getReferenceEntities(Object parentId, String... errMessage) {
		return null;
	}
	
	private void checkDataInUse(Object parentId) {
		String[] errMessage = new String[1];
		Map<Class<?>, SimpleQueryFilter[]> map =  getReferenceEntities(parentId, errMessage);
		if(map != null && !map.isEmpty()) {
			Class<?>[] classes = map.keySet().toArray(new Class[map.size()]);
			Long count = 0L;
			for(Class<?> clazz : classes) {
				SimpleQueryFilter[] filters = map.get(clazz);
				count = count + generalPurposeDao.count(clazz, filters);
			}
			if(count != null && count > 0) {
				String errMsg = "Error entity is in use";
				if(errMessage != null && errMessage.length == 1) {
					errMsg = errMessage[0];
				}
				throw new RuntimeException(errMsg);
			}
		}
	}
	
	/**
	 * delete data
	 * @param data
	 * @param pk
	 * @param pkFieldName
	 */
	protected void deleteData(final DATA data, final Serializable pk, final String pkFieldName) {
		
		TransactionTemplate tmpl = new TransactionTemplate(this.transactionManager);

		try {
			
			checkDataInUse(pk);
			
			tmpl.execute(new TransactionCallback<Integer>() {

				@Override
				public Integer doInTransaction(TransactionStatus status) {
					Object obj = null;
					try {
						obj = status.createSavepoint();
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}
					
					boolean saveCommit = true ;
					
					try {
						Map<String, Class<?>> children = getChildrenParentKeyAndEntiy();
						if(children != null && !children.isEmpty()) {
							String[] prntKeys = children.keySet().toArray(new String[children.keySet().size()]);
							for(String pKey : prntKeys) {
								Class<?> clazz = children.get(pKey);
								generalPurposeService.delete(clazz, pk, pKey);
							}
						}
						
						generalPurposeService.delete(data.getClass(), pk, pkFieldName);
						
					} catch (Exception e) {
						saveCommit = false ;
						logger.error(e.getMessage(), e);
					}
					
					if(obj != null) {
						if(saveCommit) {
							status.releaseSavepoint(obj);
						} else {
							status.rollbackToSavepoint(obj);
						}
					} else {
						if(!saveCommit) {
							status.setRollbackOnly();
						}
					}
					
					return 1;
				}
			});

			Messagebox.show(Labels.getLabel("msg.save.delete.success"), 
					Labels.getLabel("title.msgbox.information"),
					new Messagebox.Button[]{Messagebox.Button.OK},
					new String[]{Labels.getLabel("action.button.ok")},
					Messagebox.INFORMATION,
					Messagebox.Button.OK, null);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);		
			Messagebox.show(Labels.getLabel("msg.save.delete.fail") 
					+ ".\n Error: " + e.getMessage(), 
					Labels.getLabel("title.msgbox.error"),
					new Messagebox.Button[]{Messagebox.Button.OK},
					new String[]{Labels.getLabel("action.button.ok")},
					Messagebox.ERROR,
					Messagebox.Button.OK, null);
		}
		
	}
	
	/**
	 * get child/detail data info parent key dan child class
	 * @return
	 */
	protected Map<String, Class<?>> getChildrenParentKeyAndEntiy() {
		return null;
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
	
	public DATA addNewData() {
		throw new RuntimeException("Method not supported.");
	}
	
	/**
	 * sort argument
	 */
	public SimpleSortArgument[] getSorts() {
		return null ; 
	}

	public abstract Listbox getListbox()  ; 
	
	public void deleteData(DATA data) {
		throw new RuntimeException("Method not supported.");
	}

}
