package id.co.sigma.zk.ui.controller.security;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;

import id.co.sigma.common.security.domain.UserDelegation;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

/**
 * User delegation editor controller
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class UserDelegationEditorController extends BaseSimpleDirectToDBEditor<UserDelegation>{
	
	private static final long serialVersionUID = 4564427653567393752L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserDelegationEditorController.class.getName());
	
	@Wire
	@LookupEnabledControl(parameterId="DATA_STATUS_OPTIONS")
	private Combobox dataStatus;
}
