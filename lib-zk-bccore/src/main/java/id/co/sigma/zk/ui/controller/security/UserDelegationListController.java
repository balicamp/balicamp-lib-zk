package id.co.sigma.zk.ui.controller.security;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.security.domain.UserDelegation;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

/**
 * User Delegation list controller
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class UserDelegationListController extends BaseSimpleListController<UserDelegation> implements IReloadablePanel{
	
	private static final long serialVersionUID = 5107511136992905826L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserDelegationListController.class.getName());
	
	@Wire
	private Listbox searchResult;
	
	@Wire
	@LookupEnabledControl(parameterId="DATA_STATUS_OPTIONS")
	@QueryParameterEntry(filteredField="dataStatus", queryOperator=SimpleQueryFilterOperator.equal)
	private Combobox cmbStatus;
	
	@Wire
	@QueryParameterEntry(filteredField="sourceUserId", queryOperator=SimpleQueryFilterOperator.equal)
	private Textbox txtDelegateFromUser;
	
	@Wire
	@QueryParameterEntry(filteredField="destUserId", queryOperator=SimpleQueryFilterOperator.equal)
	private Textbox txtDelegateToUser;
	
	@Wire
	private Datebox txtStartDateBegin;
	
	@Wire
	private Datebox txtStartDateEnd;
	
	@Wire
	private Datebox txtEndDateBegin;
	
	@Wire
	private Datebox txtEndDateEnd;
	

	@Override
	protected Class<? extends UserDelegation> getHandledClass() {
		return UserDelegation.class;
	}

	@Override
	public Listbox getListbox() {
		return searchResult;
	}
	
	@Listen("onClick=#btnCari")
	public void onBtnCariClick(){
		invokeSearch();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		loadDefaultFormValues();
	}
	
	private void loadDefaultFormValues(){
		txtDelegateFromUser.setValue("");
		txtDelegateToUser.setValue("");
		txtStartDateBegin.setValue(new Date());
		txtStartDateEnd.setValue(new Date());
		txtEndDateBegin.setValue(new Date());
		txtEndDateEnd.setValue(new Date());
		cmbStatus.setValue("");
	}
	
	@Listen("onClick=#btnReset")
	public void onBtnResetClick(){
		loadDefaultFormValues();
	}
	
	@Listen("onClick=#btnCreateNew")
	public void onBtnCreateNewClick(){
		UserDelegation newData = new UserDelegation();
		EditorManager.getInstance().addNewData("~./zul/pages/master/security/UserDelegationEditor.zul", newData, this);
	}

	@Override
	public void reload() {
		invokeSearch();
	}
	
}
