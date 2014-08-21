package id.co.sigma.zk.ui.controller.dualcontrol;



import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;

import id.co.sigma.common.data.app.SimpleDualControlData;
import id.co.sigma.zk.ui.ZKCoreLibConstant;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;


/**
 * base class untuk menampilkan data dual control untuk  
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseDualcontrolMasterDataListController<POJO extends SimpleDualControlData<POJO>> extends BaseSimpleListController<POJO>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 600553694540665494L;
	/**
	 * data yang sedang di edit atau di view. 
	 */
	protected POJO currentData ; 
	
	
	
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		ComponentInfo inf =  super.doBeforeCompose(page, parent, compInfo);
		currentData = (POJO) Executions.getCurrent().getAttribute(ZKCoreLibConstant.EDITED_DATA_ATTRIBUTE_KEY); 
		return inf ; 
	}

}
