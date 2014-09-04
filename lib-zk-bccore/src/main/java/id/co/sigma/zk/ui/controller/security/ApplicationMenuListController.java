package id.co.sigma.zk.ui.controller.security;

import java.util.List;









import javax.annotation.Resource;
import javax.annotation.Resources;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

/**
 * 
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public class ApplicationMenuListController extends BaseSimpleController{
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(ApplicationMenuListController.class.getName());
	
	
	
	private static final SimpleSortArgument[] DEF_SORTS ={
		new SimpleSortArgument("siblingOrder", true)
	}; 
			
	
	
	@Resource(name="securityApplicationId")
	String applicationId ; 
	
	
	
	
	
	
	protected List<ApplicationMenu> getMenus () {
		Long appId = new Long(applicationId); 
		SimpleQueryFilter[] flt = new SimpleQueryFilter[]{
			 new SimpleQueryFilter("applicationId" , SimpleQueryFilterOperator.equal , appId)
		}; 
		try {
			return (List<ApplicationMenu>)generalPurposeDao.list(ApplicationMenu.class, flt, DEF_SORTS);
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.error("gagal membaca menu untuk app id : " + applicationId + " , error : " + e.getMessage() , e);
			return null ;
		} 
		
		
	}
}
