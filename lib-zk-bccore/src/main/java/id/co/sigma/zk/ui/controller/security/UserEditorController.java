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
import id.co.sigma.security.server.service.IApplicationService;
import id.co.sigma.security.server.service.IUserService;
import id.co.sigma.zk.spring.security.SecurityUtil;
import id.co.sigma.zk.ui.annotations.ChildGridData;
import id.co.sigma.zk.ui.annotations.JoinKey;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.custom.component.CustomListModel;
import id.co.sigma.zk.ui.data.SelectedRole;
import id.co.sigma.zk.ui.data.SelectedUserGroup;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;



/**
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka Darmawan</a>
 */
public class UserEditorController extends BaseSimpleDirectToDBEditor<User>{
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(UserEditorController.class.getName());
	
	
	private boolean isEditedState(){
		return ZKEditorState.EDIT.equals(getEditorState());
	}
	
	private static final SimpleSortArgument[] DEF_SORTS ={
		new SimpleSortArgument("id", true)
		};
	@Qualifier(value="securityApplicationId")
	@Autowired
	private String applicationId;
	@Autowired
	IApplicationService appService;
	
	@Autowired
	IUserService userService;
	
	ListModelList<Branch> branchModel;
	
	@Wire
	Combobox defaultBranchCode;
	
	@Wire
	Listbox listBoxCheckList;
	
	@Wire
	Listbox listBoxCheckListRole;
	
	@Wire
	Textbox email;
	
	
	@Wire
	Textbox userCode;
	
	@Wire
	Textbox chipperText;
	
	@Wire
	Checkbox status;
	
	@Wire
	Textbox confirmChipperText;
	
	@ChildGridData(
			entity = UserGroupAssignment.class,
			gridId = "listBoxCheckList",
			joinKeys={
				@JoinKey(
					parentKey = "id",
					childKey = "userId"
				)
			} 			
	)
	private ZKClientSideListDataEditorContainer<UserGroupAssignment> groups = new ZKClientSideListDataEditorContainer<UserGroupAssignment>();
	
	private void setComboValueByRealData(Combobox cmb, String data){
		ListModelList<Object> model = (ListModelList<Object>) cmb.getModel();
		if(model.getSize() > 0){
			for(Object o : model){
				Branch lov = (Branch) o;
				if(lov.getBranchCode().equalsIgnoreCase(data)){
					cmb.setValue(getEditedData().getBranch().getBranchCode()+" - "+getEditedData().getBranch().getBranchName());
					break;
				}
			}
		}
	}
	
	@ChildGridData(
			entity = UserRole.class,
			gridId = "listBoxCheckListRole",
			joinKeys={
				@JoinKey(
					parentKey = "id",
					childKey = "userId"
				)
			} 			
	)
	private ZKClientSideListDataEditorContainer<UserRole> roles = new ZKClientSideListDataEditorContainer<UserRole>();
	@Wire
	Label branchName;
	
	/*@Wire
	Textbox chipperText;*/
	
	public Listbox getListBoxCheckList() {
		return listBoxCheckList;
	}
	
	public Listbox getListBoxCheckListRole() {
		return listBoxCheckListRole;
	}
	
	CustomListModel<Branch> listBranchObject;
	
	int flag=0;
	
	public int getFlag() {
		return flag;
	}
	
	public void setFlag(int flag) {
		this.flag = flag;
	}

	private boolean isAddNewState;
	
	public boolean isAddNewState() {
		return isAddNewState;
	}
	
	/*@Listen("onSelect=#cmbdefaultBranchCode")
	public void onSelectCabang(){
		int indexCmb = cmbdefaultBranchCode.getSelectedIndex();
		if(indexCmb>=0){
			Long idBranch = cmbdefaultBranchCode.getSelectedItem().getValue();
			if(idBranch!=null){
				defaultBranchCode.setValue(idBranch);
			}
		}
		
	}*/
	
	
	@Override
	protected void doBeforeSave(User data) {
		
		validationForm(data);
	
		if(ZKEditorState.EDIT.equals(getEditorState())){
			/*if(data.getChipperText().equalsIgnoreCase("")){
				data.setChipperText(editedData.getChipperText());
			}*/
			data.setModifiedBy(SecurityUtil.getUser().getUsername());
			data.setModifiedOn(new Date());
		}else{
			data.setCreatedBy(SecurityUtil.getUser().getUsername());
			data.setCreatedOn(new Date());
			data.setModifiedBy(SecurityUtil.getUser().getUsername());
			data.setModifiedOn(new Date());
		}
		
		/*if(superAdmin.isChecked()){
			data.setSuperAdmin("Y");
		}else{
			data.setSuperAdmin("N");
		}*/
		if(status.isChecked()){
			data.setStatus("A");
		}else{
			data.setStatus("I");
		}
		data.setActiveFlag("A");
		groups.eraseData(new ArrayList<UserGroupAssignment>(groups.getAllStillExistData()));
		for(Listitem item : listBoxCheckList.getItems()) {
			UserGroupAssignment ug = new UserGroupAssignment();
			SelectedUserGroup sg = item.getValue();
			ug.setGroupId(sg.getId());
			ug.setUserId(data.getId());
			if(item.isSelected()) {
				groups.appendNewItem(ug);
			}
		}

		roles.eraseData(new ArrayList<UserRole>(roles.getAllStillExistData()));
		for(Listitem item : listBoxCheckListRole.getItems()) {
			UserRole ur = new UserRole();
			SelectedRole sr = item.getValue();
			ur.setRoleId(sr.getId());
			ur.setUserId(data.getId());
			if(item.isSelected()) {
				roles.appendNewItem(ur);
			}
		}
			
	}

	@Override
	public void deleteChildrenData(List<?> childrenData) throws Exception {
		for(Object child : childrenData) {
			if(child instanceof UserGroupAssignment) {
				generalPurposeService.delete(UserGroupAssignment.class, ((UserGroupAssignment)child).getId(), "id");
			} else if(child instanceof UserRole) {
				generalPurposeService.delete(UserRole.class, ((UserRole)child).getId(), "id");
			}
		}
	}

	
	
	@Override
	protected void insertData(Object... data) throws Exception {
		Object[] obj = data;
		if(obj != null && obj.length == 1) {
			if(obj[0] instanceof User) {
				data[0] = userService.insert((User)obj[0]);
			} else {
				super.insertData(data);
			}
		}
	}
	
	@Override
	protected void updateData(User data) throws Exception {
		userService.update(data);
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
		
		if(defaultBranchCode.getValue().equalsIgnoreCase("")){
			notValidFiled.add("Branch is empty");
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
//			Messagebox.show("Error Message : \n"+error);
			throw new RuntimeException("Error Message : \n"+error);
//			return false;
		}else{
			return true;
		}
		
	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<SelectedUserGroup> selectedUserGroup = new ArrayList<SelectedUserGroup>();
		List<SelectedRole> selectedUserRole = new ArrayList<SelectedRole>();
			
		
		List<Branch> listBranch = new ArrayList<Branch>();
		listBranch = getBranch();
		branchModel = new ListModelList<>(listBranch);
		defaultBranchCode.setModel(branchModel);
		
		if(isEditedState()){
			setComboValueByRealData(defaultBranchCode, editedData.getDefaultBranchCode());
			chipperText.setReadonly(true);
			confirmChipperText.setReadonly(true);
			chipperText.setValue(editedData.getChipperText());
			confirmChipperText.setValue(editedData.getChipperText());
			
		}
			
		if(editedData.getId()!=null){
			selectedUserRole = listSelectedUserRole(editedData.getId());
			selectedUserGroup = listSelectedUserGroup(editedData.getId());
			
			if(editedData.getStatus()!=null && editedData.getStatus().equals("A")){
				status.setChecked(true);
			}else{
				status.setChecked(false);
			}
		}else{
			selectedUserRole = getAllRole();
			selectedUserGroup = getUserGroupList();
			status.setChecked(true);
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
		groups = new ZKClientSideListDataEditorContainer<UserGroupAssignment>();
		
		if(listUserGroup!=null && !listUserGroup.isEmpty()){
			if(editedData.getId()!=null){
				listUserGroupAssignment = getUserGroupAssignmentByUserId(userId);
				groups.initiateAndFillData(listUserGroupAssignment);
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
	
	
	
	protected List<Branch> getBranch(){
		return generalPurposeDao.list(Branch.class, DEF_SORTS);
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
		
		roles = new ZKClientSideListDataEditorContainer<UserRole>();
		
		if(listUserRoleSelected!=null && !listUserRoleSelected.isEmpty()){
			if(editedData.getId()!=null){
				listUserRole = getRoleByUserId(userId);
				roles.initiateAndFillData(listUserRole);
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
	
	@Override
	protected void runAditionalTaskOnDataRevieved(User editedData,
			ZKEditorState editorState, Map<?, ?> rawDataParameter) {
		if(editorState.equals(ZKEditorState.EDIT)){
			this.flag=1;
		}
		super.runAditionalTaskOnDataRevieved(editedData, editorState, rawDataParameter);
	}
}
