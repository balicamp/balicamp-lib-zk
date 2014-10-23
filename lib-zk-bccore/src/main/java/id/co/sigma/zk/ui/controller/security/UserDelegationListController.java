package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.security.domain.UserDelegation;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;
import id.co.sigma.zk.ui.custom.component.CustomListModel;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;

/**
 * User Delegation list controller
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class UserDelegationListController extends BaseSimpleListController<UserDelegation> implements IReloadablePanel{
	
	private static final long serialVersionUID = 5107511136992905826L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserDelegationListController.class.getName());
	
	@Wire private Listbox searchResult;
	
	@LookupEnabledControl(parameterId="DATA_STATUS_OPTIONS")
	@QueryParameterEntry(filteredField="dataStatus", queryOperator=SimpleQueryFilterOperator.equal)
	@Wire private Combobox cmbStatus;
	
	@QueryParameterEntry(filteredField="sourceUserId", queryOperator=SimpleQueryFilterOperator.equal)
	@Wire private Longbox txtIdDelegateFromUser;
	
	@Wire private Combobox cmbDelegateFromUser;
	
	@QueryParameterEntry(filteredField="destUserId", queryOperator=SimpleQueryFilterOperator.equal)
	@Wire private Longbox txtIdDelegateToUser;
	
	@Wire private Combobox cmbDelegateToUser;
	
	@Wire private Datebox txtStartDateBegin;
	
	@Wire private Datebox txtStartDateEnd;
	
	@Wire private Datebox txtEndDateBegin;
	
	@Wire private Datebox txtEndDateEnd;
	
	public CustomListModel<User> getUserList() {
		CustomListModel<User> users = new CustomListModel<>(getAllUser());
		return users;
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
	
	@Override
	public UserDelegation addNewData() {
		return new UserDelegation();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}
	
	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		super.doBeforeComposeChildren(comp);
	}
	
	@Override
	public void reload() {
		invokeSearch();
	}
	
	@Listen("onSelect=#cmbDelegateFromUser")
	public void onBnbxDelegateFromUserListSelected(){
		try {
			User user = cmbDelegateFromUser.getSelectedItem().getValue();
			txtIdDelegateFromUser.setValue(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Listen("onSelect=#cmbDelegateToUser")
	public void onBnbxDelegateToUserListSelect(){
		try {
			User user = cmbDelegateToUser.getSelectedItem().getValue();
			txtIdDelegateToUser.setValue(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void resetFilter() {
		super.resetFilter();
		cmbDelegateFromUser.setValue(null);
		cmbDelegateToUser.setValue(null);
	}

	@Override
	public void invokeSearch() {
		List<SimpleQueryFilter> myFilters = new ArrayList<>();		
		SimpleQueryFilter[] filters = generateFilters();
		if(filters!=null && filters.length>0){
			for (SimpleQueryFilter simpleQueryFilter : filters) {
				myFilters.add(simpleQueryFilter);
			}
		}
		if(txtStartDateBegin.getValue()!=null && txtStartDateEnd.getValue()!=null){
			myFilters.add( new SimpleQueryFilter("startDate", txtStartDateBegin.getValue(), txtStartDateEnd.getValue()) );
		}
		if(txtEndDateBegin.getValue()!=null && txtEndDateEnd.getValue()!=null){
			myFilters.add( new SimpleQueryFilter("endDate", txtEndDateBegin.getValue(), txtEndDateEnd.getValue()) );
		}
		SimpleQueryFilter[] finalFilters = new SimpleQueryFilter[myFilters.size()];
		myFilters.toArray(finalFilters);
		SimpleSortArgument [] sorts = getSorts();
		
		super.invokeSearch(finalFilters, sorts);
	}
	
}
