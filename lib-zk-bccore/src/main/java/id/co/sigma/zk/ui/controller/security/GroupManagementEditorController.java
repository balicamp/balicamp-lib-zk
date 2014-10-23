package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.common.security.domain.ApplicationMenuAssignment;
import id.co.sigma.common.security.domain.UserGroup;
import id.co.sigma.zk.spring.security.SecurityUtil;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.data.SelectableApplicationMenu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.json.JSONArray;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;

/**
 * Group management editor controller
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class GroupManagementEditorController extends BaseSimpleDirectToDBEditor<UserGroup>{
	
	private static final long serialVersionUID = 992957201551192078L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GroupManagementEditorController.class.getName());
	
	@Qualifier(value="securityApplicationId")
	@Autowired
	private String applicationId;
	
	@Wire private Div groupMgmTree;
	
	@Wire Checkbox cbActiveFlag;
	@Wire Textbox activeFlag;
	
	@Wire Textbox selectedMenus;
	
	@Wire Textbox groupCode;
	
	private String treeData;

	public String getTreeData() {
		return treeData;
	}

	public void setTreeData(String treeData) {
		this.treeData = treeData;
	}

	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		super.doBeforeComposeChildren(comp);
		loadAllMenus();
		setTreeData(allMenus.toJSONString());
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if(getEditedData()!=null){
			cbActiveFlag.setChecked("A".equalsIgnoreCase(getEditedData().getActiveFlag()));
		}
		groupCode.setReadonly(ZKEditorState.EDIT.equals(getEditorState()));
	}

	private JSONArray allMenus = new JSONArray();
	private void loadAllMenus(){
		allMenus.clear();
		List<ApplicationMenu> menus = getMenuData();
		Long groupId = getEditedData().getId();
		
		Map<Long, SelectableApplicationMenu> tempMenus = new HashMap<Long, SelectableApplicationMenu>();
		for (ApplicationMenu menu : menus) {
			SelectableApplicationMenu swap = new SelectableApplicationMenu(menu, false);
			tempMenus.put(swap.getId(), swap);
		}
		
		// Set selected state
		if(groupId != null){
			List<ApplicationMenuAssignment> menuAssigns = getMenuAssignments(groupId);
			for (ApplicationMenuAssignment menuAssign : menuAssigns) {
				if(tempMenus.containsKey(menuAssign.getFunctionId())){
					tempMenus.get(menuAssign.getFunctionId()).getState().setSelected(true);
				}
			}
		}
		
		// Fix bug #5111
		for(SelectableApplicationMenu sam : tempMenus.values()){
			if(isEditing() && sam.getMenuTreeCode() != null){
				String[] parentIds = sam.getMenuTreeCode().split("\\.");
				int count = parentIds.length - 1;
				if( (count > 0) && sam.getState().isSelected() ){
					for (int i = 0; i < count; i++) {
						Long pid = new Long(parentIds[i]);
						if(tempMenus.containsKey(pid)){
							tempMenus.get(pid).getState().setSelected(false);
						}
					}
				}
			}
		}
		
		for(SelectableApplicationMenu sam : tempMenus.values()){
			allMenus.add(sam);
		}
	}
	
	private List<ApplicationMenu> getMenuData(){
		Long appId = new Long(applicationId);
		
		// Filters
		SimpleQueryFilter[] filtersMenus = new SimpleQueryFilter[]{
			new SimpleQueryFilter("applicationId", SimpleQueryFilterOperator.equal, appId)
		};
		
		// Sorted field(s)
		SimpleSortArgument[] sortArgs = {
			new SimpleSortArgument("functionCode", true)
		};
		
		try {
			List<ApplicationMenu> menuAll =generalPurposeDao.list(ApplicationMenu.class, filtersMenus, sortArgs); 
			if ( menuAll== null || menuAll.isEmpty()){
				return null;
			}
			return menuAll;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get menu assignments data by group ID
	 * @param groupId - Group ID
	 * @return
	 */
	private List<ApplicationMenuAssignment> getMenuAssignments(Long groupId){
		// Filters
		SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
			new SimpleQueryFilter("groupId", SimpleQueryFilterOperator.equal, groupId)
		};
		
		// Sorted field(s)
		SimpleSortArgument[] sortArgs = {
			new SimpleSortArgument("functionId", true)
		};
		
		try {
			return generalPurposeDao.list(ApplicationMenuAssignment.class, filters, sortArgs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data sec_menu_assignment! ApplicationId: " + applicationId + ", Error: " + e.getMessage(), e);
			return null;
		}
	}
	
	public List<Long> getSelectedMenuIds(){
		String[] selectedMenuIds = selectedMenus.getValue().split(",");
		List<Long> retval = new ArrayList<Long>();
		for (int i = 0; i < selectedMenuIds.length; i++) {
			Long menuId = new Long(selectedMenuIds[i]);
			retval.add(menuId);
		}
		return retval;
	}
	
	@Listen("onCheck=#cbActiveFlag")
	public void onActiveFlagChecked(){
		String isActive = cbActiveFlag.isChecked()? "A" : "I";
		activeFlag.setValue(isActive);
	}

	@Override
	protected void updateData(UserGroup data) throws Exception {
		try {
			super.updateData(data);
			saveMenuAssignment(getEditedData().getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveMenuAssignment(Long groupId){
		try {
			// delete all menu assignments data by groupId
			if(groupId!=null){
				List<ApplicationMenuAssignment> currAssigns = getMenuAssignments(groupId);
				if(currAssigns!=null && !currAssigns.isEmpty()){
					for (ApplicationMenuAssignment scn : currAssigns) {
						generalPurposeService.delete(scn);
					}
				}
			}
			
			// Selected menu id(s) from tree
			List<Long> checkedMenus = getSelectedMenuIds();
			
			// insert new menu assignments
			if(checkedMenus.size() > 0){
				// mapped selected menu ids
				Map<Long, Long> mappedMenuIds = new HashMap<>();
				for (Long mnuId : checkedMenus) {
					mappedMenuIds.put(mnuId, mnuId);
				}
				
				// include menu parent id(s)
				Long[] inFilters = new Long[checkedMenus.size()];
				checkedMenus.toArray(inFilters);
				SimpleQueryFilter[] filters = {
					new SimpleQueryFilter("id", inFilters)
				};
				
				Map<Long, Long> parenMenuIds = new HashMap<>();
				List<ApplicationMenu> appMenus = generalPurposeDao.list(ApplicationMenu.class, filters, null);
				for (ApplicationMenu menu : appMenus) {
					String[] parentIds = menu.getMenuTreeCode().split("\\.");
					for (String parentId : parentIds) {
						Long pid = new Long(parentId);
						if(!parenMenuIds.containsKey(pid) && !mappedMenuIds.containsKey(pid)){
							parenMenuIds.put(pid, pid);
						}
					}
				}
				
				// merge with selected menu ids
				for (Long parenMenuId : parenMenuIds.values()) {
					if(!mappedMenuIds.containsKey(parenMenuId)){
						mappedMenuIds.put(parenMenuId, parenMenuId);
					}
				}
				
				// saving
				for (Long mnuId : mappedMenuIds.values()) {
					ApplicationMenuAssignment assign = new ApplicationMenuAssignment();
					assign.setFunctionId(mnuId);
					assign.setGroupId(groupId);
					assign.setCreatedBy(SecurityUtil.getUser().getUsername());
					assign.setCreatedOn(new Date());
					
					generalPurposeService.insert(assign);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Listen("onClick = #btnSave")
	public void saveClick(Event evt) {
		Clients.evalJavaScript("grpMnu_getSelectedCbs();");
		super.saveClick(evt);
	}

	@Override
	public void insertData() throws Exception {
		preSaveData(true);
		Object[] data = new Object[]{getEditedData()};
		insertData(data);
		UserGroup group = (UserGroup) data[0];
		saveMenuAssignment(group.getId());
		closeCurrentEditorPanel();
	}
	
	private void preSaveData(boolean isNew){
		if(isNew){
			getEditedData().setCreatedBy(SecurityUtil.getUser().getUsername());
			getEditedData().setCreatedOn(new Date());
		}else{
			getEditedData().setModifiedBy(SecurityUtil.getUser().getUsername());
			getEditedData().setModifiedOn(new Date());
		}
		
		if(getEditedData().getApplicationId()==null){
			getEditedData().setApplicationId(new Long(applicationId));
		}
		
		if(!getEditedData().getGroupCode().isEmpty()){
			String capitalizedGroupCode = getEditedData().getGroupCode().toUpperCase();
			getEditedData().setGroupCode(capitalizedGroupCode.trim());
		}
		
		getEditedData().setSuperGroup("N");
		
		if(getEditedData().getActiveFlag()!=null && getEditedData().getActiveFlag().isEmpty()){
			getEditedData().setActiveFlag("A");
		}
	}

	@Override
	protected void validateData() throws Exception {
		if(!isEditing()){
			if( !isUnique(getEditedData().getGroupCode().trim()) ){
				throw new RuntimeException(Labels.getLabel("msg.warnings.groupmenu.not_unique"));
			}
		}
	}
	
	private boolean isEditing(){
		return (ZKEditorState.EDIT.equals(getEditorState()));
	}
	
	private boolean isUnique(String groupCode){
		SimpleQueryFilter[] filters = {
			new SimpleQueryFilter("groupCode", SimpleQueryFilterOperator.equal, groupCode)
		};
		
		Long count = generalPurposeDao.count(UserGroup.class, filters);
		
		return (count!=null && count==0L);
	}
	
}
