package id.co.sigma.zk.ui;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract  class SimpleQueryDrivenListModel<DATA> extends BaseDBDrivenListModel<DATA> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6710189159235722498L;





	private static final Logger logger = LoggerFactory.getLogger(SimpleQueryDrivenListModel.class); 

	
	
	
	
	@Autowired
	private IGeneralPurposeDao generalPurposeDao ;
	
	
	public SimpleQueryDrivenListModel(){
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
	
	@Override
	public Integer count() {
		Long swap =  generalPurposeDao.count(getHandledClass(), getFilters());
		return swap == null ? null : swap.intValue() ; 
	}

	@Override
	public List<DATA> selectFromDB(int pageSize, int firstRowPosition) {
		try {
			return generalPurposeDao.list( getHandledClass(), getFilters() , getSorts()  , pageSize, firstRowPosition);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("gagal membaca data untuk model list. error : " + e.getMessage() , e);
			return null ; 
		}
		
	}

}
