package id.co.sigma.zk.ui.controller.security;



import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;

import id.co.sigma.common.security.domain.User;
import id.co.sigma.zk.ui.controller.dualcontrol.BaseDualcontrolMasterDataListController;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class UserListController extends BaseDualcontrolMasterDataListController<User>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6628279768995608470L;
	@Wire
	Listbox userListbox ;
	
	@Override
	protected Class<? extends User> getHandledClass() {
		return User.class;
	}

	@Override
	public Listbox getListbox() {
		return userListbox;
	}
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		
		
		return super.doBeforeCompose(page, parent, compInfo);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		invokeSearch();
		
		
	}

}
