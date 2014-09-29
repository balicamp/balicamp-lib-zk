package id.co.sigma.zk.ui.controller.security;

import org.zkoss.zul.Listbox;

import id.co.sigma.common.security.domain.UserDelegation;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

/**
 * User Delegation list controller
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class UserDelegationListController extends BaseSimpleListController<UserDelegation>{
	
	private static final long serialVersionUID = 5107511136992905826L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserDelegationListController.class.getName());

	@Override
	protected Class<? extends UserDelegation> getHandledClass() {
		return UserDelegation.class;
	}

	@Override
	public Listbox getListbox() {
		// TODO Auto-generated method stub
		return null;
	}
}
