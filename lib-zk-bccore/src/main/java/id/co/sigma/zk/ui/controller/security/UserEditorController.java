package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.security.domain.UserGroup;
import id.co.sigma.common.security.domain.UserGroupAssignment;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.data.SelectedUserGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
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
	
	
	@Wire
	private Bandbox bdBranch;
	
	@Wire
	Listbox listBoxBandBox;
	
	@Wire
	Listbox listBoxCheckList;
	
	@Wire
	Textbox email;
	
	@Wire
	Textbox userCode;
	
	public Listbox getListBoxCheckList() {
		return listBoxCheckList;
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
	protected void insertData(User data) throws Exception {
		// TODO Auto-generated method stub
		try {
			if(validationForm()){
				super.insertData(data);
				saveUserGroupAssignment(data.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void updateData(User data) throws Exception {
		// TODO Auto-generated method stub
		try {
			if(validationForm()){
				super.updateData(data);
				saveUserGroupAssignment(editedData.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private boolean validationForm(){
		if(email.isValid()){
			return true;
		}else{
			Messagebox.show("Email tidak valid");
			return false;
		}
	}
	
	private void saveUserGroupAssignment(Long idUser){
		try {
			List<UserGroupAssignment> userGroupAssignment = getUserGroupAssignmentByUserId(idUser);
			if(!userGroupAssignment.isEmpty()){
				for (UserGroupAssignment uga : userGroupAssignment) {
					generalPurposeService.delete(uga);
				}
			}
			Set<Listitem> li = listBoxCheckList.getSelectedItems();
			
			List<SelectedUserGroup> listUserGroup = new ArrayList<SelectedUserGroup>();
			
			for (Listitem listitem : li) {
				listUserGroup.add((SelectedUserGroup) listitem.getValue());
			}
			
			for (SelectedUserGroup selectedUserGroup : listUserGroup) {
				UserGroupAssignment dataInsert = new UserGroupAssignment();
				dataInsert.setGroupId(selectedUserGroup.getId());
				dataInsert.setUserId(editedData.getId());
				generalPurposeService.insert(dataInsert);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal insert user_group assignment!, Error: " + e.getMessage(), e);
		}
		
		
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		
		super.doAfterCompose(comp);
		listBranchObject = new ArrayList<Branch>();
		
		listBranchObject = getDataBranch();
		if(listBranchObject!=null){
			listBoxBandBox.setModel(new ListModelList<Branch>(listBranchObject));
		}
		
		if(editedData.getId()!=null){
			listBoxCheckList.setModel(new ListModelList<SelectedUserGroup>(listSelectedUserGroup(editedData.getId())));
			listBoxCheckList.setMultiple(true);
			listBoxCheckList.setCheckmark(true);
		}else{
			listBoxCheckList.setModel(new ListModelList<SelectedUserGroup>(getUserGroupList()));
			listBoxCheckList.setMultiple(true);
			listBoxCheckList.setCheckmark(true);
		}
		
		userCode.setReadonly(getEditorState().equals(ZKEditorState.EDIT));
		
	}
	
	private List<SelectedUserGroup> listSelectedUserGroup(Long userId){
		List<SelectedUserGroup> listUserGroup =getUserGroupList();
		List<UserGroupAssignment> listUserGroupAssignment = null;
		
		List<SelectedUserGroup> listDataTampil = new ArrayList<SelectedUserGroup>();
		
		if(editedData.getId()!=null){
			listUserGroupAssignment = getUserGroupAssignmentByUserId(userId);
		}
		
		
		if(listUserGroup!=null && !listUserGroup.isEmpty()){
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
	
}
