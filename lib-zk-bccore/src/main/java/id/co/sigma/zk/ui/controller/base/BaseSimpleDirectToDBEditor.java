package id.co.sigma.zk.ui.controller.base;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.service.IGeneralPurposeService;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleDirectToDBEditor<POJO extends Serializable> extends BaseSimpleEditor<POJO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5283385493029490963L;
	
	
	
	@Autowired
	private IGeneralPurposeService generalPurposeService ;  
	
	@Autowired
	IGeneralPurposeDao  generalPurposeDao ; 
	
	
	@Override
	protected void insertData(POJO data) throws Exception {
		generalPurposeService.insert(data);
		
	}
	
	@Override
	protected void updateData(POJO data) throws Exception {
		generalPurposeService.update(data);
	}
	
	

}
