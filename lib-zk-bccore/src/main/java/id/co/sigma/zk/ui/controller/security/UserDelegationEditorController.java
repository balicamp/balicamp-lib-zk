package id.co.sigma.zk.ui.controller.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.User;
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
	
	@Wire
	private Bandbox bnbxDelegateFromUser;
	
	@Wire
	private Longbox sourceUserId;
	
	@Wire
	private Bandbox bnbxDelegateToUser;
	
	@Wire
	private Longbox destUserId;
	
	@Wire
	private Listbox lbAvailableRoles;
	
	@Wire
	private Listbox lbDelegatedRoles;
	
	@Wire
	private Listbox lbAvailableGroups;
	
	@Wire
	private Listbox lbDelegatedGroups;
	
	@Wire private Listbox bnbxDelegateFromUserList;
	@Listen("onSelect=#bnbxDelegateFromUserList")
	public void onBnbxDelegateFromUserListSelected(){
		try {
			User user = bnbxDelegateFromUserList.getSelectedItem().getValue();
			sourceUserId.setValue(user.getId());
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
	
	private void moveListboxItem(Listbox source, Listbox dest, boolean moveAll){
		if(moveAll){
			// FIXME ini masih error "java.util.ConcurrentModificationException "
			int j = source.getItems().size();			
			for (int i = 0; i < j; i++) {
				source.setSelectedItem(source.getItemAtIndex(i));
				dest.getItems().add(source.getSelectedItem());
			}
		}else{
			if(source.getSelectedItem() != null){
				dest.getItems().add(source.getSelectedItem());
			}else{
				source.setSelectedItem(source.getItemAtIndex(0));
				dest.getItems().add(source.getSelectedItem());
			}			
		}
	}
	
	@Listen("onClick=#btnAddAllRoles")
	public void btnAddAllRoles_click(){
		moveListboxItem(lbAvailableRoles, lbDelegatedRoles, true);
	}
	
	@Listen("onClick=#btnAddSingleRole")
	public void btnAddSingleRole_click(){
		moveListboxItem(lbAvailableRoles, lbDelegatedRoles, false);
	}
	
	@Listen("onClick=#btnRemoveSingleRole")
	public void btnRemoveSingleRole_click(){
		moveListboxItem(lbDelegatedRoles, lbAvailableRoles, false);
	}
	
	@Listen("onClick=#btnRemoveAllRoles")
	public void btnRemoveAllRoles_click(){
		moveListboxItem(lbDelegatedRoles, lbAvailableRoles, true);
	}
	
	@Listen("onClick=#btnAddAllGroups")
	public void btnAddAllGroups_click(){
		moveListboxItem(lbAvailableGroups, lbDelegatedGroups, true);
	}
	
	@Listen("onClick=#btnAddSingleGroup")
	public void btnAddSingleGroup_click(){
		moveListboxItem(lbAvailableGroups, lbDelegatedGroups, false);
	}
	
	@Listen("onClick=#btnRemoveSingleGroup")
	public void btnRemoveSingleGroup_click(){
		moveListboxItem(lbDelegatedGroups, lbAvailableGroups, false);
	}
	
	@Listen("onClick=#btnRemoveAllGroups")
	public void btnRemoveAllGroups_click(){
		moveListboxItem(lbDelegatedGroups, lbAvailableGroups, true);
	}
	
}
