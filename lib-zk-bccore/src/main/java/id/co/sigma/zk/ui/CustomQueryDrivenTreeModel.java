/**
 * 
 */
package id.co.sigma.zk.ui;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;


/**
 * 
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public abstract class CustomQueryDrivenTreeModel<DATA> extends BaseDBDrivenListModel<DATA> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4991882562476104482L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(CustomQueryDrivenTreeModel.class.getName());
	
	
	@Autowired
	private IGeneralPurposeDao generalPurposeDao ;
	
	
	public CustomQueryDrivenTreeModel(){
		super() ; 
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}
	
	
	
	
	/**
	 * class yang di load
	 */
	public abstract Class<? extends DATA> getHandledClass () ;
	
	
	
	
	/**
	 * filter query
	 */
	protected abstract SimpleQueryFilter[] getFilters()  ;
	
	
	/**
	 * sorting arguments
	 */
	protected abstract SimpleSortArgument[] getSorts() ; 
	
	
	/**
	 * custom query
	 */
	protected abstract String getCustomQuery();
	
	/**
	 * initial class
	 */
	protected abstract String getInitial();
	
	@Override
	public Integer count() {
		Long swap =  generalPurposeDao.count(getHandledClass(), getFilters());
		return swap == null ? null : swap.intValue() ; 
	}

	@Override
	public List<DATA> selectFromDB(int pageSize, int firstRowPosition) {
		try {
			return generalPurposeDao.list(getCustomQuery(),getInitial(), getFilters() , getSorts()  , pageSize, firstRowPosition);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("gagal membaca data untuk model list. error : " + e.getMessage() , e);
			return null ; 
		}
		
	}

	
}
