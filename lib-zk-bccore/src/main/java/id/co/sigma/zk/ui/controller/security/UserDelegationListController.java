package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.security.domain.UserDelegation;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;

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
	private Longbox txtIdDelegateFromUser;
	
	@Wire
	private Bandbox bnbxDelegateFromUser;
	
	@Wire
	@QueryParameterEntry(filteredField="destUserId", queryOperator=SimpleQueryFilterOperator.equal)
	private Longbox txtIdDelegateToUser;
	
	@Wire
	private Bandbox bnbxDelegateToUser;
	
	@Wire
	private Datebox txtStartDateBegin;
	
	@Wire
	private Datebox txtStartDateEnd;
	
	@Wire
	private Datebox txtEndDateBegin;
	
	@Wire
	private Datebox txtEndDateEnd;
	
	public List<User> getUserList() {
		return new ListModelList<>(getAllUser());
	}
	
	private List<User> getAllUser(){
		SimpleQueryFilter[] filters = {
			new SimpleQueryFilter("status", SimpleQueryFilterOperator.equal, "A")	
		};
		
		SimpleSortArgument[] sortArgs = {
			new SimpleSortArgument("realName", true)
		};
		
		try {
			return generalPurposeDao.list(User.class, filters, sortArgs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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
	
	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		super.doBeforeComposeChildren(comp);
	}
	
	private void loadDefaultFormValues(){
		txtIdDelegateFromUser.setValue(null);
		bnbxDelegateFromUser.setValue("");
		txtIdDelegateToUser.setValue(null);
		bnbxDelegateToUser.setValue("");
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
	
	@Wire private Listbox bnbxDelegateFromUserList;
	@Listen("onSelect=#bnbxDelegateFromUserList")
	public void onBnbxDelegateFromUserListSelected(){
		try {
			User user = bnbxDelegateFromUserList.getSelectedItem().getValue();
			txtIdDelegateFromUser.setValue(user.getId());
			bnbxDelegateFromUser.setValue(user.getRealName());
			bnbxDelegateFromUser.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Wire private Listbox bnbxDelegateToUserList;
	@Listen("onSelect=#bnbxDelegateToUserList")
	public void onBnbxDelegateToUserListSelect(){
		try {
			User user = bnbxDelegateToUserList.getSelectedItem().getValue();
			txtIdDelegateToUser.setValue(user.getId());
			bnbxDelegateToUser.setValue(user.getRealName());			
			bnbxDelegateToUser.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
