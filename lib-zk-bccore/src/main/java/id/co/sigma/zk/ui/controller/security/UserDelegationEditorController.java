package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.lov.CommonLOV;
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
import id.co.sigma.zk.spring.security.SecurityUtil;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;

/**
 * User delegation editor controller
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class UserDelegationEditorController extends BaseSimpleDirectToDBEditor<UserDelegation>{
	
	private static final long serialVersionUID = 4564427653567393752L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserDelegationEditorController.class.getName());
	
	private final String defaultDataStatus = "A";
	
	private final String constraintDateFormat = "yyyyMMdd";
	
	private final String moduleTitle = "User Delegation Editor";
	
	@LookupEnabledControl(parameterId="DATA_STATUS_OPTIONS")
	@Wire private Combobox dataStatus;
	
	@Wire private Bandbox bnbxDelegateFromUser;
	
	@Wire private Longbox sourceUserId;
	
	@Wire private Bandbox bnbxDelegateToUser;
	
	@Wire private Longbox destUserId;
	
	@Wire private Datebox startDate;
	
	@Wire private Datebox endDate;
	
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
	
	private void clearListbox(Listbox lb){
		lb.getItems().clear();
	}
	
	private boolean fromAndToUserIsSame(Long fromUserId, Long toUserId){
		if(fromUserId==null || toUserId==null){
			return false;
		}
		
		return (fromUserId.compareTo(toUserId)==0);
	}
	
	/**
	 * Bandbox delegate from user onSelect handler
	 */
	@Wire private Listbox bnbxDelegateFromUserList;
	@SuppressWarnings("unchecked")
	@Listen("onSelect=#bnbxDelegateFromUserList")
	public void onBnbxDelegateFromUserListSelected(){
		try {
			User user = bnbxDelegateFromUserList.getSelectedItem().getValue();
			if(!fromAndToUserIsSame(user.getId(), destUserId.getValue())){
				// Set values and close bandbox
				sourceUserId.setValue(user.getId());
				bnbxDelegateFromUser.setValue(user.getRealName());
				bnbxDelegateFromUser.close();
				
				// Update listbox
				lbAvailableRoles.setModel((ListModel<UserRole>) getUserRoleListModel());
				clearListbox(lbDelegatedRoles);
				lbAvailableGroups.setModel((ListModel<UserGroupAssignment>) getUserGroupListModel());
				clearListbox(lbDelegatedGroups);
				
				// Button state
				if(lbAvailableRoles.getItems().size()>0){
					btnAddAllRoles.setDisabled(false);
					btnAddSingleRole.setDisabled(false);
				}
				if(lbAvailableGroups.getItems().size()>0){
					btnAddAllGroups.setDisabled(false);
					btnAddSingleGroup.setDisabled(false);
				}
			}else{
				Messagebox.show("User asal dan tujuan tidak boleh sama!", moduleTitle, Messagebox.OK, Messagebox.EXCLAMATION);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Bandbox delegate to user onSelect handler
	 */
	@Wire private Listbox bnbxDelegateToUserList;
	@Listen("onSelect=#bnbxDelegateToUserList")
	public void onBnbxDelegateToUserListSelected(){
		try {
			User user = bnbxDelegateToUserList.getSelectedItem().getValue();
			if(!fromAndToUserIsSame(sourceUserId.getValue(), user.getId())){
				destUserId.setValue(user.getId());
				bnbxDelegateToUser.setValue(user.getRealName());			
				bnbxDelegateToUser.close();
			}else{
				Messagebox.show("User asal dan tujuan tidak boleh sama!", moduleTitle, Messagebox.OK, Messagebox.EXCLAMATION);
			}
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
		int j = source.getItems().size();
		
		if(moveAll){
			if(j > 0){
				List<Listitem> copy = new ArrayList<Listitem>(source.getItems());
				for (Listitem item : copy) {
					dest.getItems().add(item);
				}
				source.getItems().clear();
			}
		}else{
			if(j > 0){
				if(source.getSelectedItem() != null){
					dest.getItems().add(source.getSelectedItem());
				}else{
					source.setSelectedItem(source.getItemAtIndex(0));
					dest.getItems().add(source.getSelectedItem());
				}
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
		Long userId = isEditing()? getEditedData().getSourceUserId() : sourceUserId.getValue();
		
		if(userId==null){
			return null;
		}
		
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
	
	private List<UserDelegationRole> getDelegatedRoles(){
		try {
			return userDelegationService.getRoles(getEditedData().getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<UserGroupAssignment> getUserGroups(){
		Long userId = isEditing()? getEditedData().getSourceUserId() : sourceUserId.getValue();
		
		if(userId==null){
			return null;
		}
		
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
	
	private List<UserDelegationGroup> getDelegatedGroups(){
		try {
			return userDelegationService.getGroups(getEditedData().getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<UserRole> getUserRoleListModel(){
		return new ListModelList<>(getUserRoles());
	}
	
	public List<UserRole> getRemainingUserRoleListModel(){
		// Delegated roles
		List<UserDelegationRole> delegatedRoles = getDelegatedRoles();
		Map<Long, UserDelegationRole> delegatedRolesMap = new HashMap<>();
		if(delegatedRoles!=null && !delegatedRoles.isEmpty()){
			for(UserDelegationRole delegatedRole : delegatedRoles){
				delegatedRolesMap.put(delegatedRole.getRoleId(), delegatedRole);
			}
		}
		
		// All user roles
		List<UserRole> retval = new ArrayList<>();
		List<UserRole> allRoles = getUserRoles();
		if(allRoles!=null && !allRoles.isEmpty()){
			for(UserRole urole : allRoles){
				if(!delegatedRolesMap.containsKey(urole.getRoleId())){
					retval.add(urole);
				}
			}
		}else{
			return new ListModelList<>();
		}
		
		return new ListModelList<>(retval);
	}
	
	public List<UserRole> getDelegatedUserRoleListModel(){
		// Delegated roles
		List<UserDelegationRole> currentRoles = getDelegatedRoles();
		Map<Long, UserDelegationRole> currentRolesMap = new HashMap<>();
		if(currentRoles!=null && !currentRoles.isEmpty()){
			for(UserDelegationRole delegRole : currentRoles){
				currentRolesMap.put(delegRole.getRoleId(), delegRole);
			}
		}else{
			return new ListModelList<>();
		}
		
		// All user roles
		List<UserRole> retval = new ArrayList<>();
		List<UserRole> userRoles = getUserRoles();
		if(userRoles!=null){
			for (UserRole userRole : userRoles) {
				if(currentRolesMap.containsKey(userRole.getRoleId())){
					retval.add(userRole);
				}
			}
		}
		
		return new ListModelList<>(retval);
	}
	
	public List<UserGroupAssignment> getUserGroupListModel(){
		return new ListModelList<>(getUserGroups());
	}
	
	public List<UserGroupAssignment> getRemainingUserGroupListModel(){
		// Delegated groups
		List<UserDelegationGroup> delegatedGroups = getDelegatedGroups();
		Map<Long, UserDelegationGroup> delegatedGroupsMap = new HashMap<>();
		if(delegatedGroups!=null && !delegatedGroups.isEmpty()){
			for(UserDelegationGroup group : delegatedGroups){
				delegatedGroupsMap.put(group.getGroupId(), group);
			}
		}
		
		// All user groups
		List<UserGroupAssignment> retval = new ArrayList<>();
		List<UserGroupAssignment> userGroups = getUserGroups();
		if(userGroups!=null){
			for(UserGroupAssignment grpAss : userGroups){
				if(!delegatedGroupsMap.containsKey(grpAss.getGroupId())){
					retval.add(grpAss);
				}
			}
		}else{
			return new ListModelList<>();
		}
		
		return new ListModelList<>(retval);
	}
	
	public List<UserGroupAssignment> getDelegatedUserGroupListModel(){
		// Delegated groups
		List<UserDelegationGroup> currentGroups = getDelegatedGroups();
		Map<Long, UserDelegationGroup> currentGroupsMap = new HashMap<>();
		if(currentGroups!=null && !currentGroups.isEmpty()){
			for(UserDelegationGroup group : currentGroups){
				currentGroupsMap.put(group.getGroupId(), group);
			}
		}else{
			return new ListModelList<>();
		}
		
		// All user groups
		List<UserGroupAssignment> retval = new ArrayList<>();
		List<UserGroupAssignment> userGroups = getUserGroups();
		if(userGroups!=null){
			for(UserGroupAssignment grpAss : userGroups){
				if(currentGroupsMap.containsKey(grpAss.getGroupId())){
					retval.add(grpAss);
				}
			}
		}
		
		return new ListModelList<>(retval);
	}
	
	/**
	 * Cek apakah delegasi dari user x dalam rentang waktu tertentu sudah ada/belum
	 * @param userId - ID user asal
	 * @param startDate - Dari tanggal
	 * @param endDate - Sampai tanggal
	 * @return boolean
	 */
	private boolean overlappingUserDelegationIsExist(Long userId, Date startDate, Date endDate){
		SimpleQueryFilter[] filters = {
			new SimpleQueryFilter("sourceUserId", SimpleQueryFilterOperator.equal, userId),
			new SimpleQueryFilter("endDate", startDate, endDate)
		};
		
		Long delegs = generalPurposeDao.count(UserDelegation.class, filters);
		
		return ( (delegs!=null) && (delegs>0) );
	}
	
	private boolean delegationIsUnique(Long srcUserId, Long destUserId){
		SimpleQueryFilter[] filters = {
			new SimpleQueryFilter("sourceUserId", SimpleQueryFilterOperator.equal, srcUserId),
			new SimpleQueryFilter("destUserId", SimpleQueryFilterOperator.equal, destUserId)
		};
			
		Long delegs = generalPurposeDao.count(UserDelegation.class, filters);
		
		return ( (delegs!=null) && (delegs==0) );
	}
	
	private void disableExistingDelegations(Long userId, Date startDate, Date endDate){
		SimpleQueryFilter[] filters = {
			new SimpleQueryFilter("sourceUserId", SimpleQueryFilterOperator.equal, userId),
			new SimpleQueryFilter("endDate", startDate, endDate)
		};
		
		try {
			List<UserDelegation> delegs = generalPurposeDao.list(UserDelegation.class, filters, null);
			if(delegs!=null && !delegs.isEmpty()){
				for (UserDelegation deleg : delegs) {
					deleg.setDataStatus("I");
					generalPurposeService.merge(deleg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void insertData(Object... data) throws Exception {
		getEditedData().setCreatedBy(SecurityUtil.getUser().getUsername());
		getEditedData().setCreatedOn(new Date());
		userDelegationService.insert(getEditedData(), getDelegatedRolesFromListbox(), getDelegatedGroupsFromListbox());
	}
	
	@Override
	@Listen("onClick = #btnSave")
	public void saveClick(final Event evt) {
		parseEditedData(evt.getTarget());
		try{
			bindValueFromControl(getEditedData());
		}catch(Exception e){
			logger.error("Gagal simpam data. error : " +e.getMessage() , e ); 
			showErrorMessage(getEditorState(), e.getMessage());
			return;
		}
		
		String confirmMsg = (String) getSelf().getAttribute("confirmationMsg");
		Messagebox.show(confirmMsg, Labels.getLabel("title.msgbox.confirmation"),
				new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO},
				new String[]{Labels.getLabel("action.button.yes"), Labels.getLabel("action.button.no")},
				Messagebox.QUESTION,
				Messagebox.Button.YES,
				new EventListener<Messagebox.ClickEvent>() {
			
			@Override
			public void onEvent(Messagebox.ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					saveDataWorker(evt);
				}
			}
		});	
	}

	@Override
	protected void updateData(UserDelegation data) throws Exception {
		userDelegationService.update(getEditedData(), getDelegatedRolesFromListbox(), getDelegatedGroupsFromListbox());
	}
	
	private void saveDataWorker(final Event evt){
		// Editing
		if(isEditing()){
			saveData(evt);
		}else{
			
			// Inserting
			boolean isUnique = delegationIsUnique(getEditedData().getSourceUserId(), getEditedData().getDestUserId());
			
			if(!isEditing() && isUnique){
				boolean overlappingDelegationsExists = overlappingUserDelegationIsExist(getEditedData().getSourceUserId(), getEditedData().getStartDate(), getEditedData().getEndDate());
				if(overlappingDelegationsExists){
					String msg = "Delegasi dari user yang sama dalam rentang waktu "+startDate.getText()+
								 " s/d "+endDate.getText()+" sudah ada, klik OK untuk menonaktifkan delegasi-delegasi yang terdahulu dan menyimpan data.";
					Messagebox.show(msg, "Perhatian!", Messagebox.OK|Messagebox.CANCEL, Messagebox.EXCLAMATION, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							if(event.getName().equals(Messagebox.ON_OK)){
								// Disable existing delegation(s)
								disableExistingDelegations(getEditedData().getSourceUserId(), getEditedData().getStartDate(), getEditedData().getEndDate());
								saveData(evt);
							}
						}
					});
				}else{
					saveData(evt);
				}
			}
			
			if(!isEditing() && !isUnique){
				Messagebox.show("Delegasi dari user dan ke user yang sama sudah ada!", moduleTitle, Messagebox.OK, Messagebox.EXCLAMATION);
			}
		
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
	
	private void setComboValueByRealData(Combobox cmb, String data){
		ListModelList<Object> model = (ListModelList<Object>) cmb.getModel();
		if(model.getSize() > 0){
			for(Object o : model){
				CommonLOV lov = (CommonLOV) o;
				if(lov.getAdditionalData1().equalsIgnoreCase(data)){
					cmb.setValue(lov.getLabel());
					break;
				}
			}
		}
	}
	
	private boolean isEditing(){
		return (ZKEditorState.EDIT.equals(getEditorState()));
	}
	
	/**
	 * Menyesuaikan field-field pada form editor dengan data saat ini
	 */
	private void adjustEditorFields(){
		if(isEditing()){
			// Set value combo data status sesuai dgn data dari db
			if(!getEditedData().getDataStatus().isEmpty()){
				setComboValueByRealData(dataStatus, getEditedData().getDataStatus());
			}
			
			// Set start date constraint
			startDate.setConstraint("no empty, after "+dateToString(getEditedData().getStartDate(), constraintDateFormat));
			
			// Set end date constraint
			endDate.setConstraint("no empty, after "+dateToString(getNextDayDate(getEditedData().getStartDate()), constraintDateFormat));
			
			// Show available roles (remaining only)
			lbAvailableRoles.setModel((ListModel<?>) getRemainingUserRoleListModel());
			
			// Show delegated roles
			if(getDelegatedRoles()!=null){
				lbDelegatedRoles.setModel((ListModel<?>) getDelegatedUserRoleListModel());
			}else{
				lbDelegatedRoles.setModel(new ListModelList<>());
			}
			
			// Show available groups (remaining only)
			lbAvailableGroups.setModel((ListModel<?>) getRemainingUserGroupListModel());
			
			// Show delegated groups
			if(getDelegatedGroups()!=null){
				lbDelegatedGroups.setModel((ListModel<?>) getDelegatedUserGroupListModel());
			}else{
				lbDelegatedGroups.setModel(new ListModelList<>());
			}
		}else{
			// Set value combo data status sesuai dgn data dari db
			setComboValueByRealData(dataStatus, defaultDataStatus);
			
			// Set start date value & constraint
			startDate.setValue(new Date());
			startDate.setConstraint("no empty, no past");
			
			// Set end date value & constraint
			endDate.setValue(getTomorrowDate());
			endDate.setConstraint("no empty, no past, after "+getTomorrowDateStr());
			
			// Disable roles & group buttons
			btnAddAllRoles.setDisabled(true);
			btnAddSingleRole.setDisabled(true);
			btnRemoveAllRoles.setDisabled(true);
			btnRemoveSingleRole.setDisabled(true);
			btnAddAllGroups.setDisabled(true);
			btnAddSingleGroup.setDisabled(true);
			btnRemoveAllGroups.setDisabled(true);
			btnRemoveSingleGroup.setDisabled(true);
		}
	}
	
	private Date getTomorrowDate(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	/**
	 * Get next day from specific date
	 * @param dt - The specific date
	 * @return (specific date + 1 day)
	 */
	private Date getNextDayDate(Date dt){
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	public String getTomorrowDateStr(){
		return dateToString(getTomorrowDate(), constraintDateFormat);
	}
	
	private String dateToString(Date dt, String format){
		Format formatter = new SimpleDateFormat(format);
		return formatter.format(dt);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		adjustEditorFields();
	}

}
