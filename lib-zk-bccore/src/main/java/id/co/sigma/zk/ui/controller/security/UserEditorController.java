package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.common.security.domain.Role;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.security.domain.UserGroup;
import id.co.sigma.common.security.domain.UserGroupAssignment;
import id.co.sigma.common.security.domain.UserRole;
import id.co.sigma.security.server.service.IUserService;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.data.SelectedRole;
import id.co.sigma.zk.ui.data.SelectedUserGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka Darmawan</a>
 */
public class UserEditorController extends BaseSimpleDirectToDBEditor<User>{
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(UserEditorController.class.getName());
	
	@Autowired
	IUserService userService;
	
	@Wire
	private Bandbox bdBranch;
	
	@Wire
	Listbox listBoxBandBox;
	
	@Wire
	Listbox listBoxCheckList;
	
	@Wire
	Listbox listBoxCheckListRole;
	
	@Wire
	Textbox email;
	
	@Wire
	Textbox userCode;
	
	@Wire
	Checkbox superAdmin;
	
	@Wire
	Checkbox status;
	
	@Wire
	Textbox confirmChipperText;
	
	/*@Wire
	Textbox chipperText;*/
	
	public Listbox getListBoxCheckList() {
		return listBoxCheckList;
	}
	
	public Listbox getListBoxCheckListRole() {
		return listBoxCheckListRole;
	}
	
	private List<Branch> listBranchObject;

	
	public List<Branch> getListBranchObject() {
		return listBranchObject;
	}

	private boolean isAddNewState;
	
	public boolean isAddNewState() {
		return isAddNewState;
	}
	
	@Override
	public void insertData() throws Exception {
		/*// TODO Auto-generated method stub
		super.insertData();*/
		System.out.println("Masuk tanpa parameter");
	}
	
	@Override
	@Listen("onClick = #btnSave")
	public void saveClick(final Event evt) {
		
		String confirmMsg = (String)getSelf().getAttribute("confirmationMsg");
		if(confirmMsg != null && confirmMsg.trim().length() > 0) {
			
			Messagebox.show(confirmMsg, "Prompt", 
					Messagebox.YES|Messagebox.NO, 
					Messagebox.QUESTION, 
			new EventListener<Event>() {
				
				@Override
				public void onEvent(Event event) throws Exception {
					switch(((Integer)event.getData()).intValue()) {
					case Messagebox.YES:
						saveData(evt);
						break;
					case Messagebox.NO:
						break;
					}
				}
			});				
		} else saveData(evt); 
		
		
	}
	
	
	private final void saveData(final Event evt) {
		parseEditedData(evt.getTarget());
		User data = getEditedData();
		if(validationForm(data)){
			
			if(ZKEditorState.EDIT.equals(getEditorState())){
				if(data.getChipperText().equalsIgnoreCase("")){
					data.setChipperText(editedData.getChipperText());
				}
			}
			
			if(superAdmin.isChecked()){
				data.setSuperAdmin("Y");
			}else{
				data.setSuperAdmin("N");
			}
			if(status.isChecked()){
				data.setStatus("Y");
			}else{
				data.setStatus("N");
			}
			
			Set<Listitem> li = listBoxCheckList.getSelectedItems();
			
			Set<Listitem> liRole = listBoxCheckListRole.getSelectedItems();
			
			List<SelectedUserGroup> selectedListUserGroup = new ArrayList<SelectedUserGroup>();
			List<SelectedRole> selectedListRole = new ArrayList<SelectedRole>();
			
			List<UserGroupAssignment> listGroup = new ArrayList<UserGroupAssignment>();
			List<UserRole> listUserRole = new ArrayList<UserRole>();
			
			for (Listitem listitem : li) {
				selectedListUserGroup.add((SelectedUserGroup) listitem.getValue());
			}
			
			for (Listitem listitem : liRole) {
				selectedListRole.add((SelectedRole) listitem.getValue());
			}
			
			for (SelectedUserGroup selectedUserGroup : selectedListUserGroup) {
				UserGroupAssignment dataInsert = new UserGroupAssignment();
				dataInsert.setGroupId(selectedUserGroup.getId());
				if(ZKEditorState.EDIT.equals(getEditorState())){
					dataInsert.setUserId(editedData.getId());
				}
				listGroup.add(dataInsert);
			}
		
			
			for (SelectedRole selectedRole : selectedListRole) {
				UserRole dataUserRole = new UserRole();
				dataUserRole.setRoleId(selectedRole.getId());
				if(ZKEditorState.EDIT.equals(getEditorState())){
					dataUserRole.setUserId(editedData.getId());
				}
				listUserRole.add(dataUserRole);
				
			}
			
			try {
				userService.insertDataUser(data, listGroup, listUserRole);
				EditorManager.getInstance().closeCurrentEditorPanel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*@Override
	protected void insertData(User data) throws Exception {
		if(validationForm()){
			
			Set<Listitem> li = listBoxCheckList.getSelectedItems();
			
			Set<Listitem> liRole = listBoxCheckListRole.getSelectedItems();
			
			List<SelectedUserGroup> selectedListUserGroup = new ArrayList<SelectedUserGroup>();
			List<SelectedRole> selectedListRole = new ArrayList<SelectedRole>();
			
			List<UserGroupAssignment> listGroup = new ArrayList<UserGroupAssignment>();
			List<UserRole> listUserRole = new ArrayList<UserRole>();
			
			for (Listitem listitem : li) {
				selectedListUserGroup.add((SelectedUserGroup) listitem.getValue());
			}
			
			for (Listitem listitem : liRole) {
				selectedListRole.add((SelectedRole) listitem.getValue());
			}
			
			for (SelectedUserGroup selectedUserGroup : selectedListUserGroup) {
				UserGroupAssignment dataInsert = new UserGroupAssignment();
				dataInsert.setGroupId(selectedUserGroup.getId());
				dataInsert.setUserId(editedData.getId());
				listGroup.add(dataInsert);
			}
		
			for (SelectedRole selectedRole : selectedListRole) {
				UserRole dataUserRole = new UserRole();
				dataUserRole.setRoleId(selectedRole.getId());
				dataUserRole.setUserId(selectedRole.getId());
				listUserRole.add(dataUserRole);
				
			}
			
			userService.insertDataUser(data, listGroup, listUserRole);
			
		}
	}*/
	
	@Override
	protected void updateData(User data) throws Exception {
		// TODO Auto-generated method stub
		/*if(validationForm()){
			saveUserGroupAssignment(editedData.getId(), data);
		}*/
	}
	private boolean validationForm(User data){
		List<String> notValidFiled = new ArrayList<String>();
		
		if(ZKEditorState.ADD_NEW.equals(getEditorState())){
			if(data.getChipperText().equalsIgnoreCase("")){
				notValidFiled.add("Password is empty");
			}
		}
		
		if(!data.getChipperText().equals(confirmChipperText.getValue())){
			notValidFiled.add("Password confirmation is incorrect");
		}
		if(!email.isValid()){
			notValidFiled.add("Invalid Email Address");
		}
		
		if(notValidFiled.size()>0){
			String error = "";
			for (String string : notValidFiled) {
				if(error.equals("")){
					error = string+" \n";
				}else{
					error += string+" \n";
				}
			}
			Messagebox.show("Error Message : \n"+error);
			return false;
		}else{
			return true;
		}
		
	}
	
	/*private void saveUserGroupAssignment(Long idUser, User data){
			if(getEditorState().equals(ZKEditorState.ADD_NEW)){
				try {
					super.insertData(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			if(getEditorState().equals(ZKEditorState.EDIT)){
				try {
					super.updateData(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			List<UserGroupAssignment> userGroupAssignment = getUserGroupAssignmentByUserId(idUser);
			if(!userGroupAssignment.isEmpty()){
				for (UserGroupAssignment uga : userGroupAssignment) {
					try {
						generalPurposeService.delete(uga);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			Set<Listitem> li = listBoxCheckList.getSelectedItems();
			
			Set<Listitem> liRole = listBoxCheckListRole.getSelectedItems();
			
			List<SelectedUserGroup> listUserGroup = new ArrayList<SelectedUserGroup>();
			List<SelectedRole> listRole = new ArrayList<SelectedRole>();
			
			for (Listitem listitem : li) {
				listUserGroup.add((SelectedUserGroup) listitem.getValue());
			}
			
			for (Listitem listitem : liRole) {
				listRole.add((SelectedRole) listitem.getValue());
			}
			
			for (SelectedUserGroup selectedUserGroup : listUserGroup) {
				UserGroupAssignment dataInsert = new UserGroupAssignment();
				dataInsert.setGroupId(selectedUserGroup.getId());
				dataInsert.setUserId(editedData.getId());
				try {
					generalPurposeService.insert(dataInsert);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		
			for (SelectedRole selectedRole : listRole) {
				UserRole dataUserRole = new UserRole();
				dataUserRole.setRoleId(selectedRole.getId());
				dataUserRole.setUserId(selectedRole.getId());
				try {
					generalPurposeService.insert(dataUserRole);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		
		
	}*/
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		
		super.doAfterCompose(comp);
		listBranchObject = new ArrayList<Branch>();
		
		List<SelectedUserGroup> selectedUserGroup = new ArrayList<SelectedUserGroup>();
		
				
		List<SelectedRole> selectedUserRole = new ArrayList<SelectedRole>();
			
		
		listBranchObject = getDataBranch();
		if(listBranchObject!=null){
			listBoxBandBox.setModel(new ListModelList<Branch>(listBranchObject));
		}
		
		if(editedData.getId()!=null){
			selectedUserRole = listSelectedUserRole(editedData.getId());
			selectedUserGroup = listSelectedUserGroup(editedData.getId());
			if(editedData.getSuperAdmin()!=null && editedData.getSuperAdmin().equals("Y")){
				superAdmin.setChecked(true);
			}else{
				superAdmin.setChecked(false);
			}
			
			if(editedData.getStatus()!=null && editedData.getStatus().equals("Y")){
				status.setChecked(true);
			}else{
				status.setChecked(false);
			}
		}else{
			selectedUserRole = getAllRole();
			selectedUserGroup = getUserGroupList();
		}
		listBoxCheckList.setModel(new ListModelList<SelectedUserGroup>(selectedUserGroup));
		listBoxCheckList.setMultiple(true);
		listBoxCheckList.setCheckmark(true);
		
		listBoxCheckListRole.setModel(new ListModelList<SelectedRole>(selectedUserRole));
		listBoxCheckListRole.setMultiple(true);
		listBoxCheckListRole.setCheckmark(true);
		
		userCode.setReadonly(getEditorState().equals(ZKEditorState.EDIT));
		
	}
	
	private List<SelectedUserGroup> listSelectedUserGroup(Long userId){
		List<SelectedUserGroup> listUserGroup =getUserGroupList();
		List<UserGroupAssignment> listUserGroupAssignment = null;
		
		List<SelectedUserGroup> listDataTampil = new ArrayList<SelectedUserGroup>();
		
		if(listUserGroup!=null && !listUserGroup.isEmpty()){
			if(editedData.getId()!=null){
				listUserGroupAssignment = getUserGroupAssignmentByUserId(userId);
			}
			for (SelectedUserGroup selectedUserGroup : listUserGroup) {
				if(listUserGroupAssignment != null && !listUserGroupAssignment.isEmpty()){
					for (UserGroupAssignment userGroupAssignment : listUserGroupAssignment) {
						if(selectedUserGroup.getId().compareTo(userGroupAssignment.getGroupId())== 0){
							selectedUserGroup.setSelected(true);
							break;
						}
					}
				}else{
					selectedUserGroup.setSelected(false);
				}
				
				listDataTampil.add(selectedUserGroup);
			}
		}
		return listDataTampil;
	}
	
	
	private List<Branch> getDataBranch(){
		List<Branch> dataListBranch = new ArrayList<Branch>();
		SimpleSortArgument[] sortArgs = {
			new SimpleSortArgument("branchCode", true)
		};
		try {
			dataListBranch = generalPurposeDao.list(Branch.class, sortArgs);
			System.out.println("jumlah List Branch : "+dataListBranch.size());
			return dataListBranch;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data sec_branch!, Error: " + e.getMessage(), e);
			return null;
		}
	}
	
	private List<SelectedUserGroup> getUserGroupList(){
		List<UserGroup> listUserGroup = new ArrayList<UserGroup>();
		List<SelectedUserGroup> listSelectedUserGroup = new ArrayList<SelectedUserGroup>();
		
		SimpleSortArgument[] sortArgs = {
				new SimpleSortArgument("groupCode", true)
		};
		try {
			listUserGroup = generalPurposeDao.list(UserGroup.class, sortArgs);
			System.out.println("jumlah List User Group : "+listUserGroup.size());
			if(listUserGroup!=null && !listUserGroup.isEmpty()){
				for (UserGroup data : listUserGroup) {
					listSelectedUserGroup.add(new SelectedUserGroup(data));
				}
			}
			return listSelectedUserGroup;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data Sec_group!, Error: " + e.getMessage(), e);
			return null;
		}
	}
	
	private List<UserGroupAssignment> getUserGroupAssignmentByUserId(Long userId){
		List<UserGroupAssignment> listUserGroupAssignment = new ArrayList<UserGroupAssignment>();
		
		SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
				new SimpleQueryFilter("userId", SimpleQueryFilterOperator.equal, userId)
		};
		
		SimpleSortArgument[] sortArgs ={
			new SimpleSortArgument("groupId", true)
		};
		
		try {
			listUserGroupAssignment = generalPurposeDao.list(UserGroupAssignment.class, filters, sortArgs);
			return listUserGroupAssignment;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data Sec_group_assignment!, Error: " + e.getMessage(), e);
			return null;
		}
		
		
	}
	
	
	private List<UserRole> getRoleByUserId(Long userId){
		List<UserRole> listUserRole = new ArrayList<UserRole>();
		
		SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
				new SimpleQueryFilter("userId", SimpleQueryFilterOperator.equal, userId)
		};
		
		SimpleSortArgument[] sortArgs ={
				new SimpleSortArgument("roleId", true)
			};
		try {
			listUserRole = generalPurposeDao.list(UserRole.class, filters, sortArgs);
			/*listUserRole = userService.getUserRoleByUserId(userId);*/
			return listUserRole;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<SelectedRole> getAllRole(){
		List<Role> listRole = new ArrayList<Role>();
		List<SelectedRole> listSelectedRole = new ArrayList<SelectedRole>();
		
		SimpleSortArgument[] sortArgs = {
				new SimpleSortArgument("roleCode", true)
		};
		try {
			listRole = generalPurposeDao.list(Role.class, sortArgs);
			if(listRole!=null && !listRole.isEmpty()){
				for (Role data : listRole) {
					listSelectedRole.add(new SelectedRole(data));
				}
			}
			return listSelectedRole;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data Sec_group!, Error: " + e.getMessage(), e);
			return null;
		}
	}
	
	private List<SelectedRole> listSelectedUserRole(Long userId){
		List<SelectedRole> listUserRoleSelected =getAllRole();
		List<UserRole> listUserRole = null;
		
		List<SelectedRole> listDataTampil = new ArrayList<SelectedRole>();
		
		if(listUserRoleSelected!=null && !listUserRoleSelected.isEmpty()){
			if(editedData.getId()!=null){
				listUserRole = getRoleByUserId(userId);
			}
			for (SelectedRole selectedUserRole : listUserRoleSelected) {
				if(listUserRole != null && !listUserRole.isEmpty()){
					for (UserRole dataUserRole : listUserRole) {
						if(selectedUserRole.getId().compareTo(dataUserRole.getRoleId())== 0){
							selectedUserRole.setSelected(true);
							break;
						}
					}
				}else{
					selectedUserRole.setSelected(false);
				}
				
				listDataTampil.add(selectedUserRole);
			}
		}
		return listDataTampil;
	}
}
