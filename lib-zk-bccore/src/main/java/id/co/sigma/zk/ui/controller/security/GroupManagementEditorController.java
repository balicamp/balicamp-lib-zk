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
		
		if(groupId != null){
			List<ApplicationMenuAssignment> menuAssigns = getMenuAssignments(groupId);
			for (ApplicationMenuAssignment menuAssign : menuAssigns) {
				if(tempMenus.containsKey(menuAssign.getFunctionId())){
					tempMenus.get(menuAssign.getFunctionId()).getState().setSelected(true);
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
		String isActive = cbActiveFlag.isChecked()? "A" : "D";
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
			
			// insert new menu assignments
			List<Long> checkedMenus = getSelectedMenuIds();
			if(checkedMenus.size() > 0){
				for (Long mnuId : checkedMenus) {
					ApplicationMenuAssignment assign = new ApplicationMenuAssignment();
					assign.setFunctionId(mnuId);
					assign.setGroupId(groupId);
					assign.setCreatedBy("GSR"); // FIXME Nanti harus diisi dengan user yg login
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
			getEditedData().setGroupCode(capitalizedGroupCode);
		}
		
		getEditedData().setSuperGroup("N");
		
		if(getEditedData().getActiveFlag()!=null && getEditedData().getActiveFlag().isEmpty()){
			getEditedData().setActiveFlag("A");
		}
	}
	
}
