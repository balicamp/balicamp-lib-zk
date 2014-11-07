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
import id.co.sigma.common.server.dao.util.ServerSideDateTimeParser;
import id.co.sigma.security.server.service.IUserDelegationService;
import id.co.sigma.zk.spring.security.SecurityUtil;
import id.co.sigma.zk.ui.annotations.ChildGridData;
import id.co.sigma.zk.ui.annotations.DualListboxBinder;
import id.co.sigma.zk.ui.annotations.EqualsField;
import id.co.sigma.zk.ui.annotations.JoinKey;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.custom.component.CustomListModel;
import id.co.sigma.zk.ui.custom.component.DualListbox;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Longbox;

/**
 * User delegation editor controller
 * 
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class UserDelegationEditorController extends BaseSimpleDirectToDBEditor<UserDelegation> {

    private static final long serialVersionUID = 4564427653567393752L;

    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserDelegationEditorController.class.getName());

    @Autowired
    @Qualifier("commonUiConstraintDateFormat")
    private String constraintDateFormat;

    @LookupEnabledControl(parameterId = "DATA_STATUS_OPTIONS")
    @Wire
    private Combobox dataStatus;

    @Wire
    private Combobox cmbDelegateFromUser;

    @Wire
    private Longbox sourceUserId;

    @Wire
    private Combobox cmbDelegateToUser;

    @Wire
    private Longbox destUserId;

    @Wire
    private Datebox startDate;

    @Wire
    private Datebox endDate;

    @Autowired
    private IUserDelegationService userDelegationService;

    @DualListboxBinder(sourceClass = UserGroupAssignment.class, targetClass = UserDelegationGroup.class, equalsFields = { @EqualsField(sourceField = "groupId", targetField = "groupId") })
    @Wire
    private DualListbox dlbDelegateGroup;

    @DualListboxBinder(sourceClass = UserRole.class, targetClass = UserDelegationRole.class, equalsFields = { @EqualsField(sourceField = "roleId", targetField = "roleId") })
    @Wire
    private DualListbox dlbDelegateRole;

    private boolean hasOverlappingDelegation = false;

    private List<UserRole> userSourceRoles;
    private List<UserGroupAssignment> userSourceGroups;

    @ChildGridData(entity = UserDelegationGroup.class, gridId = "dlbDelegateGroup", joinKeys = { @JoinKey(childKey = "userDelegateId", parentKey = "id") })
    private ZKClientSideListDataEditorContainer<UserDelegationGroup> delegateGroups;

    @ChildGridData(entity = UserDelegationGroup.class, gridId = "dlbDelegateRole", joinKeys = { @JoinKey(childKey = "userDelegateId", parentKey = "id") })
    private ZKClientSideListDataEditorContainer<UserDelegationRole> delegateRole;

    private boolean fromAndToUserIsSame(Long fromUserId, Long toUserId) {
	if (fromUserId == null || toUserId == null) {
	    return false;
	}

	return (fromUserId.compareTo(toUserId) == 0);
    }

    /**
     * @return the delegateGroups
     */
    public ZKClientSideListDataEditorContainer<UserDelegationGroup> getDelegateGroups() {
	return delegateGroups;
    }

    /**
     * @param delegateGroups
     *            the delegateGroups to set
     */
    public void setDelegateGroups(
	    ZKClientSideListDataEditorContainer<UserDelegationGroup> delegateGroups) {
	this.delegateGroups = delegateGroups;
    }

    /**
     * @return the delegateRole
     */
    public ZKClientSideListDataEditorContainer<UserDelegationRole> getDelegateRole() {
	return delegateRole;
    }

    /**
     * @param delegateRole
     *            the delegateRole to set
     */
    public void setDelegateRole(
	    ZKClientSideListDataEditorContainer<UserDelegationRole> delegateRole) {
	this.delegateRole = delegateRole;
    }

    /**
     * @return the userSourceRoles
     */
    public List<UserRole> getUserSourceRoles() {
	return userSourceRoles;
    }

    /**
     * @param userSourceRoles
     *            the userSourceRoles to set
     */
    public void setUserSourceRoles(List<UserRole> userSourceRoles) {
	this.userSourceRoles = userSourceRoles;
    }

    /**
     * @return the userSourceGroups
     */
    public List<UserGroupAssignment> getUserSourceGroups() {
	return userSourceGroups;
    }

    /**
     * @param userSourceGroups
     *            the userSourceGroups to set
     */
    public void setUserSourceGroups(List<UserGroupAssignment> userSourceGroups) {
	this.userSourceGroups = userSourceGroups;
    }

    /**
     * Bandbox delegate from user onChanging handler
     */
    /*
     * @Listen("onChanging=#bnbxDelegateFromUser") public void
     * bnbxDelegateFromUser_changing(InputEvent ie){ try{ String txt =
     * ie.getValue(); if(txt!=null && txt.length()>=3){ List<User> users =
     * getAllUserByName(txt); if(users!=null && users.size()>0){
     * bnbxDelegateFromUserList.setModel(new ListModelList<>(users)); } }else{
     * bnbxDelegateFromUserList.setModel(new ListModelList<>(getAllUser())); }
     * }catch(Exception e){ e.printStackTrace(); } }
     */

    /**
     * Bandbox delegate from user onBlur handler
     */
    /*
     * @Listen("onBlur=#bnbxDelegateFromUser") public void
     * bnbxDelegateFromUser_blur(){ Long srcUserId = sourceUserId.getValue();
     * if(srcUserId==null || srcUserId==0L){
     * bnbxDelegateFromUser.setValue(null); bnbxDelegateFromUser.setText(null);
     * } }
     */

    /**
     * Combobox delegate from user onSelect handler
     */
    // @Wire private Listbox bnbxDelegateFromUserList;
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Listen("onSelect=#cmbDelegateFromUser")
    public void onCmbDelegateFromUserSelected() {
	try {
	    User user = cmbDelegateFromUser.getSelectedItem().getValue();
	    if (!fromAndToUserIsSame(user.getId(), destUserId.getValue())) {
		// Set values and close bandbox
		sourceUserId.setValue(user.getId());
		// cmbDelegateFromUser.setValue(user.getRealName());
		// cmbDelegateFromUser.close();

		this.userSourceRoles = new ArrayList<UserRole>(getUserRoles());
		this.delegateRole = new ZKClientSideListDataEditorContainer<UserDelegationRole>();
		dlbDelegateRole
			.setTargetContainer((ZKClientSideListDataEditorContainer) delegateRole);
		dlbDelegateRole.setSrcModel((List<?>) this.userSourceRoles);
		dlbDelegateRole.loadData();

		this.userSourceGroups = new ArrayList<UserGroupAssignment>(
			getUserGroups());
		this.delegateGroups = new ZKClientSideListDataEditorContainer<UserDelegationGroup>();
		dlbDelegateGroup
			.setTargetContainer((ZKClientSideListDataEditorContainer) delegateGroups);
		dlbDelegateGroup.setSrcModel((List<?>) this.userSourceGroups);
		dlbDelegateGroup.loadData();

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Combobox delegate to user onSelect handler
     */
    // @Wire private Listbox bnbxDelegateToUserList;
    @Listen("onSelect=#cmbDelegateToUser")
    public void onCmbDelegateToUserSelected(Event ev) {
	try {
	    User user = cmbDelegateToUser.getSelectedItem().getValue();
	    destUserId.setValue(user.getId());

	    /*
	     * if(!fromAndToUserIsSame(sourceUserId.getValue(), user.getId())){
	     * destUserId.setValue(user.getId()); }else{
	     * destUserId.setValue(null); }
	     */
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public CustomListModel<User> getUserList() {
	CustomListModel<User> users = new CustomListModel<>(getAllUser());
	return users;
    }

    private List<User> getAllUser() {
	SimpleQueryFilter[] filters = { new SimpleQueryFilter("status",
		SimpleQueryFilterOperator.equal, "A") };

	SimpleSortArgument[] sortArgs = { new SimpleSortArgument("realName",
		true) };

	try {
	    return generalPurposeDao.list(User.class, filters, sortArgs);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /*
     * private List<User> getAllUserByName(String name){ if(name==null) name="";
     * 
     * SimpleQueryFilter[] filters = { new SimpleQueryFilter("status",
     * SimpleQueryFilterOperator.equal, "A"), new SimpleQueryFilter("realName",
     * SimpleQueryFilterOperator.likeBothSide, name) };
     * 
     * SimpleSortArgument[] sortArgs = { new SimpleSortArgument("realName",
     * true) };
     * 
     * try { return generalPurposeDao.list(User.class, filters, sortArgs); }
     * catch (Exception e) { e.printStackTrace(); return null; } }
     */

    private List<UserRole> getUserRoles() {
	Long userId = isEditing() ? getEditedData().getSourceUserId()
		: sourceUserId.getValue();

	if (userId == null) {
	    return null;
	}

	SimpleQueryFilter[] filters = { new SimpleQueryFilter("userId",
		SimpleQueryFilterOperator.equal, userId) };

	try {
	    return generalPurposeDao.list(UserRole.class, filters, null);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    private List<UserDelegationRole> getDelegatedRoles() {
	try {
	    return userDelegationService.getRoles(getEditedData().getId());
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    private List<UserGroupAssignment> getUserGroups() {
	Long userId = isEditing() ? getEditedData().getSourceUserId()
		: sourceUserId.getValue();

	if (userId == null) {
	    return null;
	}

	SimpleQueryFilter[] filters = { new SimpleQueryFilter("userId",
		SimpleQueryFilterOperator.equal, userId) };

	try {
	    return generalPurposeDao.list(UserGroupAssignment.class, filters,
		    null);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    private List<UserDelegationGroup> getDelegatedGroups() {
	try {
	    return userDelegationService.getGroups(getEditedData().getId());
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * Cek apakah ada delegasi yang aktif dari user x dalam rentang waktu
     * tertentu sudah ada/belum
     * 
     * @param userId
     *            - ID user asal
     * @param startDate
     *            - Dari tanggal
     * @param endDate
     *            - Sampai tanggal
     * @return boolean
     */
    private boolean overlappingUserDelegationIsExist(Long userId, Date startDate, Date endDate) {
	SimpleQueryFilter[] filters = {
		new SimpleQueryFilter("sourceUserId", SimpleQueryFilterOperator.equal, userId),
		new SimpleQueryFilter("dataStatus", SimpleQueryFilterOperator.equal, "A"),
		new SimpleQueryFilter("endDate", startDate, endDate)
	};

	Long delegs = generalPurposeDao.count(UserDelegation.class, filters);

	return ((delegs != null) && (delegs > 0L));
    }

    private boolean delegationIsUnique(Long srcUserId, Long destUserId, Date startDate, Date endDate) {
	SimpleQueryFilter[] filters = {
		new SimpleQueryFilter("sourceUserId", SimpleQueryFilterOperator.equal, srcUserId),
		new SimpleQueryFilter("destUserId", SimpleQueryFilterOperator.equal, destUserId),
		new SimpleQueryFilter("startDate", SimpleQueryFilterOperator.equal, startDate),
		new SimpleQueryFilter("endDate", SimpleQueryFilterOperator.equal, endDate),
		new SimpleQueryFilter("dataStatus", SimpleQueryFilterOperator.equal, "A") 
	};

	Long delegs = generalPurposeDao.count(UserDelegation.class, filters);

	return ((delegs != null) && (delegs == 0L));
    }

    private void disableExistingDelegations(Long userId, Date startDate,
	    Date endDate) {
	SimpleQueryFilter[] filters = {
		new SimpleQueryFilter("sourceUserId",
			SimpleQueryFilterOperator.equal, userId),
		new SimpleQueryFilter("endDate", startDate, endDate) };

	try {
	    List<UserDelegation> delegs = generalPurposeDao.list(
		    UserDelegation.class, filters, null);
	    if (delegs != null && !delegs.isEmpty()) {
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
    protected void validateData() throws Exception {
	if (fromAndToUserIsSame(sourceUserId.getValue(), destUserId.getValue())) {
	    throw new RuntimeException(
		    Labels.getLabel("msg.warnings.delegation.same_delegated_user"));
	}

	if (!isEditing()) {
	    if ( delegationIsUnique(sourceUserId.getValue(), destUserId.getValue(), startDate.getValue(), endDate.getValue()) ) {
		hasOverlappingDelegation = overlappingUserDelegationIsExist(sourceUserId.getValue(), startDate.getValue(), endDate.getValue());
		if (hasOverlappingDelegation) {
		    String confirmMsg = Labels.getLabel("msg.warnings.delegation.overlapping");
		    confirmMsg = confirmMsg.replace("{startingDate}", startDate.getText());
		    confirmMsg = confirmMsg.replace("{endingDate}", endDate.getText());
		    getSelf().setAttribute("confirmationMsg", confirmMsg);
		}else{
		    getSelf().setAttribute("confirmationMsg", Labels.getLabel("msg.questions.confirm_save"));
		}
	    } else {
		String exMsg = Labels.getLabel("msg.warnings.delegation.not_unique");
		exMsg = exMsg.replace("{srcUserId}", cmbDelegateFromUser.getValue());
		exMsg = exMsg.replace("{destUserId}", cmbDelegateToUser.getValue());
		throw new RuntimeException(exMsg);
	    }
	}
    }

    @Override
    protected void doBeforeSave(UserDelegation editedData) {
	if (!isEditing()) {
	    getEditedData().setCreatedBy(SecurityUtil.getUser().getUsername());
	    getEditedData().setCreatedOn(new Date());
	}
    }

    @Override
    protected void saveData(Event event) {
	if (hasOverlappingDelegation) {
	    disableExistingDelegations(sourceUserId.getValue(),
		    startDate.getValue(), endDate.getValue());
	}
	super.saveData(event);
    }

    private boolean isEditing() {
	return (ZKEditorState.EDIT.equals(getEditorState()));
    }

    /**
     * Menyesuaikan field-field pada form editor dengan data saat ini
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void adjustEditorFields() {
	cmbDelegateFromUser.setDisabled(isEditing());
	cmbDelegateToUser.setDisabled(isEditing());
	startDate.setDisabled(isEditing());
	endDate.setDisabled(isEditing());

	if (isEditing()) {

	    cmbDelegateFromUser.setValue(getEditedData().getSourceUser().getRealName());
	    cmbDelegateToUser.setValue(getEditedData().getDestUser().getRealName());

	    this.userSourceRoles = new ArrayList<UserRole>(getUserRoles());
	    this.delegateRole = new ZKClientSideListDataEditorContainer<UserDelegationRole>();
	    this.delegateRole.initiateAndFillData(getDelegatedRoles());
	    dlbDelegateRole.setTargetContainer((ZKClientSideListDataEditorContainer) delegateRole);
	    dlbDelegateRole.setSrcModel((List<?>) this.userSourceRoles);
	    dlbDelegateRole.loadData();

	    this.userSourceGroups = new ArrayList<UserGroupAssignment>(getUserGroups());
	    this.delegateGroups = new ZKClientSideListDataEditorContainer<UserDelegationGroup>();
	    this.delegateGroups.initiateAndFillData(getDelegatedGroups());
	    dlbDelegateGroup.setTargetContainer((ZKClientSideListDataEditorContainer) delegateGroups);
	    dlbDelegateGroup.setSrcModel((List<?>) this.userSourceGroups);
	    dlbDelegateGroup.loadData();

	} else {

	    // Set start date value & constraint
	    startDate.setValue(new Date());
	    startDate.setConstraint("no empty, no past");

	    // Set end date value & constraint
	    endDate.setValue(getTomorrowDate());
	    endDate.setConstraint("no empty, no past, after " + getTomorrowDateStr());
	    
	    // Set status = A (active)
	    dataStatus.setValue("A");
	}
    }

    private Date getTomorrowDate() {
	Calendar cal = Calendar.getInstance();
	cal.add(Calendar.DATE, 1);
	return cal.getTime();
    }

    public String getTomorrowDateStr() {
	return ServerSideDateTimeParser.getInstance().format(getTomorrowDate(),
		constraintDateFormat);
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
	super.doAfterCompose(comp);
	adjustEditorFields();
    }

    @Override
    protected String getSelectedLOV(Combobox cmb) {
	if (cmb.equals(dataStatus)) {
	    return getEditedData().getDataStatus();
	}
	return "";
    }

}
