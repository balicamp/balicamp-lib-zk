/**
 * 
 */
package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.zk.ui.CustomQueryDrivenTreeModel;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;



/**
 * Base Controller untuk tree
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public abstract class BaseSimpleTreeController<DATA extends Serializable> extends BaseSimpleListController<DATA> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1191738033988376462L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(BaseSimpleTreeController.class.getName());
	
	
	@Autowired
	@Qualifier(value="securityApplicationId")
	String applicationId ;
	
	
	public abstract Tree getTree();
	
	CustomQueryDrivenTreeModel<DATA> dataModel;
	
	protected abstract String getCustomQuery();
	
	protected abstract String getInitial();
	

	@Override
	public void invokeSearch() {
		final SimpleQueryFilter[] filters = generateFilters() ;  
		SimpleSortArgument [] sorts = getSorts(); 
		invokeSearch(filters, sorts);
	}

	@Override
	public void invokeSearch(final SimpleQueryFilter[] filters,
			final SimpleSortArgument[] sorts) {
		final Class<DATA> dt = (Class<DATA>) getHandledClass();  
		final String customQuery = getCustomQuery();
		final String initial = getInitial();
		
		dataModel  = generateTreeModel(filters,sorts);
		Tree tree = getTree(); 
		dataModel.initiate(tree.getPageSize());
		List<DATA> retVal = dataModel.getHoldedData();
		if(retVal!=null){
			for(DATA data : retVal){
				System.out.println(data.toString());
			}
		}
		tree.setModel(constructTree(retVal));
	}
	
	
	protected CustomQueryDrivenTreeModel<DATA> generateTreeModel (final SimpleQueryFilter[] filters,
			final SimpleSortArgument[] sorts) {
		final Class<DATA> dt = (Class<DATA>) getHandledClass();  
		final String customQuery = getCustomQuery();
		final String initial = getInitial();
		return new CustomQueryDrivenTreeModel<DATA>() {
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
			@Override
			protected String getCustomQuery() {
				return customQuery;
			}
			@Override
			protected String getInitial() {
				return initial;
			}
		};
	}
	
	
	
	public abstract TreeModel<TreeNode<DATA>> constructTree(List<DATA> data);
	
	
	@Override
	protected SimpleQueryFilter[] generateFilters() {
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
		Long appId = new Long(applicationId);
		flts.add(new SimpleQueryFilter("applicationId" , SimpleQueryFilterOperator.equal , appId));
		SimpleQueryFilter[] retval = new SimpleQueryFilter[flts.size()]; 
		flts.toArray(retval);
		return retval ;
	}


	@Override
	public Listbox getListbox() {
		return null;
	}

	@Override
	protected Class<? extends DATA> getHandledClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimpleSortArgument[] getSorts() {
		SimpleSortArgument[] sorts ={
				new SimpleSortArgument("id", true)
				}; 
		return sorts;
	}
	
	

}
