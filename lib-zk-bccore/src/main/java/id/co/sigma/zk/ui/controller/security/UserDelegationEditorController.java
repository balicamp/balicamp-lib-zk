package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.security.domain.UserDelegation;
import id.co.sigma.common.security.domain.UserDelegationGroup;
import id.co.sigma.common.security.domain.UserDelegationRole;
import id.co.sigma.common.security.domain.UserGroupAssignment;
import id.co.sigma.common.security.domain.UserRole;
import id.co.sigma.security.server.service.IUserDelegationService;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;

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
	
	@Wire
	private Bandbox bnbxDelegateFromUser;
	
	@Wire
	private Longbox sourceUserId;
	
	@Wire
	private Bandbox bnbxDelegateToUser;
	
	@Wire
	private Longbox destUserId;
	
	@Autowired
	private IUserDelegationService userDelegationService;
	
	// User roles stuff
	@Wire private Listbox lbAvailableRoles;
	@Wire private Listbox lbDelegatedRoles;
	@Wire private Button btnAddAllRoles;
	@Wire private Button btnAddSingleRole;
	@Wire private Button btnRemoveSingleRole;
	@Wire private Button btnRemoveAllRoles;
	
	// User groups stuff
	@Wire private Listbox lbAvailableGroups;
	@Wire private Listbox lbDelegatedGroups;
	@Wire private Button btnAddAllGroups;
	@Wire private Button btnAddSingleGroup;
	@Wire private Button btnRemoveSingleGroup;
	@Wire private Button btnRemoveAllGroups;
	
	@Wire private Listbox bnbxDelegateFromUserList;
	@SuppressWarnings("unchecked")
	@Listen("onSelect=#bnbxDelegateFromUserList")
	public void onBnbxDelegateFromUserListSelected(){
		try {
			User user = bnbxDelegateFromUserList.getSelectedItem().getValue();
			sourceUserId.setValue(user.getId());
			bnbxDelegateFromUser.setValue(user.getRealName());
			bnbxDelegateFromUser.close();
			lbAvailableRoles.setModel((ListModel<UserRole>) getUserRoleListModel());
			lbAvailableGroups.setModel((ListModel<UserGroupAssignment>) getUserGroupListModel());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Wire private Listbox bnbxDelegateToUserList;
	@Listen("onSelect=#bnbxDelegateToUserList")
	public void onBnbxDelegateToUserListSelected(){
		try {
			User user = bnbxDelegateToUserList.getSelectedItem().getValue();
			destUserId.setValue(user.getId());
			bnbxDelegateToUser.setValue(user.getRealName());			
			bnbxDelegateToUser.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
	
	private void moveListboxItem(Listbox source, Listbox dest, boolean moveAll, boolean isMovingRoles, boolean ltr){
		if(moveAll){
			int j = source.getItems().size();
			if(j > 0){
				List<Listitem> copy = new ArrayList<Listitem>(source.getItems());
				for (Listitem item : copy) {
					dest.getItems().add(item);
				}
				source.getItems().clear();
			}
		}else{
			if(source.getSelectedItem() != null){
				dest.getItems().add(source.getSelectedItem());
			}else{
				source.setSelectedItem(source.getItemAtIndex(0));
				dest.getItems().add(source.getSelectedItem());
			}			
		}
		
		updateGroupsAndRolesButtonState(source, isMovingRoles, ltr);
	}
	
	private void updateGroupsAndRolesButtonState(Listbox source, boolean isRoleButtons, boolean ltr){
		int listCount = source.getItems().size();
		
		if(isRoleButtons){
			btnAddAllRoles.setDisabled(listCount == 0 && ltr);
			btnAddSingleRole.setDisabled(listCount == 0 && ltr);
			btnRemoveSingleRole.setDisabled(listCount == 0 && !ltr);
			btnRemoveAllRoles.setDisabled(listCount == 0 && !ltr);
		}else{
			btnAddAllGroups.setDisabled(listCount == 0 && ltr);
			btnAddSingleGroup.setDisabled(listCount == 0 && ltr);
			btnRemoveSingleGroup.setDisabled(listCount == 0 && !ltr);
			btnRemoveAllGroups.setDisabled(listCount == 0 && !ltr);
		}
	}
	
	@Listen("onClick=#btnAddAllRoles")
	public void btnAddAllRoles_click(){
		moveListboxItem(lbAvailableRoles, lbDelegatedRoles, true, true, true);
	}
	
	@Listen("onClick=#btnAddSingleRole")
	public void btnAddSingleRole_click(){
		moveListboxItem(lbAvailableRoles, lbDelegatedRoles, false, true, true);
	}
	
	@Listen("onClick=#btnRemoveSingleRole")
	public void btnRemoveSingleRole_click(){
		moveListboxItem(lbDelegatedRoles, lbAvailableRoles, false, true, false);
	}
	
	@Listen("onClick=#btnRemoveAllRoles")
	public void btnRemoveAllRoles_click(){
		moveListboxItem(lbDelegatedRoles, lbAvailableRoles, true, true, false);
	}
	
	@Listen("onClick=#btnAddAllGroups")
	public void btnAddAllGroups_click(){
		moveListboxItem(lbAvailableGroups, lbDelegatedGroups, true, false, true);
	}
	
	@Listen("onClick=#btnAddSingleGroup")
	public void btnAddSingleGroup_click(){
		moveListboxItem(lbAvailableGroups, lbDelegatedGroups, false, false, true);
	}
	
	@Listen("onClick=#btnRemoveSingleGroup")
	public void btnRemoveSingleGroup_click(){
		moveListboxItem(lbDelegatedGroups, lbAvailableGroups, false, false, false);
	}
	
	@Listen("onClick=#btnRemoveAllGroups")
	public void btnRemoveAllGroups_click(){
		moveListboxItem(lbDelegatedGroups, lbAvailableGroups, true, false, false);
	}
	
	private List<UserRole> getUserRoles(){
		Long userId = sourceUserId.getValue();
		
		SimpleQueryFilter[] filters = {
			new SimpleQueryFilter("userId", SimpleQueryFilterOperator.equal, userId)	
		};
		
		try {
			return generalPurposeDao.list(UserRole.class, filters, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<UserGroupAssignment> getUserGroups(){
		Long userId = sourceUserId.getValue();
		
		SimpleQueryFilter[] filters = {
			new SimpleQueryFilter("userId", SimpleQueryFilterOperator.equal, userId)	
		};
		
		try {
			return generalPurposeDao.list(UserGroupAssignment.class, filters, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<UserRole> getUserRoleListModel(){
		return new ListModelList<>(getUserRoles());
	}
	
	public List<UserGroupAssignment> getUserGroupListModel(){
		return new ListModelList<>(getUserGroups());
	}

	@Override
	protected void insertData(UserDelegation data) throws Exception {
		try {
			userDelegationService.insert(getEditedData(), getDelegatedRolesFromListbox(), getDelegatedGroupsFromListbox());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void updateData(UserDelegation data) throws Exception {
		try {
			userDelegationService.update(getEditedData(), getDelegatedRolesFromListbox(), getDelegatedGroupsFromListbox());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<UserDelegationRole> getDelegatedRolesFromListbox(){
		int itemCount = lbDelegatedRoles.getItems().size();
		List<UserDelegationRole> retval = null;
		if(itemCount > 0){
			retval = new ArrayList<UserDelegationRole>();
			for(Listitem li : lbDelegatedRoles.getItems()){
				UserRole userRole = li.getValue();
				UserDelegationRole role = new UserDelegationRole(userRole);
				retval.add(role);
			}
		}
		return retval;
	}
	
	private List<UserDelegationGroup> getDelegatedGroupsFromListbox(){
		int itemCount = lbDelegatedGroups.getItems().size();
		List<UserDelegationGroup> retval = null;
		if(itemCount > 0){
			retval = new ArrayList<UserDelegationGroup>();
			for(Listitem li : lbDelegatedGroups.getItems()){
				UserGroupAssignment grpAsg = li.getValue();
				UserDelegationGroup group = new UserDelegationGroup(grpAsg);
				retval.add(group);
			}
		}
		return retval;
	}
	
	@Listen("onSelect=#dataStatus")
	public void onDataStatusSelected(){
		Comboitem item = dataStatus.getSelectedItem();
		System.out.println("Comboitem.value=" + item.getValue());
		System.out.println("Comboitem.label=" + item.getLabel());
	}
	
}
