package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.common.security.domain.ApplicationMenuAssignment;
import id.co.sigma.common.security.domain.UserGroup;
import id.co.sigma.zk.tree.MenuTreeNode;
import id.co.sigma.zk.tree.MenuTreeNodeCollection;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.data.SelectedApplicationMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;

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
	
	@Wire Checkbox cbSuperGroup;
	@Wire Textbox superGroup;
	@Wire Tree groupMgmTree;
	
	@Override
	protected void insertData(UserGroup data) throws Exception {
		try {
			getEditedData().setApplicationId(new Long(applicationId));
			super.insertData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Listen("onCheck=#cbSuperGroup")
	public void onSuperGroupChecked(){
		String isSuperGroup = cbSuperGroup.isChecked()? "Y" : "N";
		superGroup.setValue(isSuperGroup);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		UserGroup data = getEditedData();
		if(data != null){
			boolean checked = false;
			if(data.getSuperGroup()!=null){
				checked = data.getSuperGroup().equalsIgnoreCase("Y");
			}
			cbSuperGroup.setChecked(checked);
		}		
	}
	
	public DefaultTreeModel<SelectedApplicationMenu> getTreeModel(){
		return new DefaultTreeModel<SelectedApplicationMenu>(getTreeNodes());
	}
	
	private List<SelectedApplicationMenu> rootMenus = new ArrayList<SelectedApplicationMenu>();
	private Map<Long, List<SelectedApplicationMenu>> childMenus  = new HashMap<>();
	private Map<Long, ApplicationMenu> selectedMenus = new HashMap<>();
	
	private List<SelectedApplicationMenu> getMenusByParentId (Long idParent ){
		return childMenus.get(idParent); 
	}
	
	private void loadAllMenus () {
		if(getEditedData()!=null && getEditedData().getId()!=null){
			List<ApplicationMenuAssignment> menuAss = getMenuAssignments(getEditedData().getId());
			for (ApplicationMenuAssignment menuAs : menuAss) {
				selectedMenus.put(menuAs.getFunctionId(), menuAs.getFunction());
			}
		}
		
		List<SelectedApplicationMenu> menus = getMenus();
		for(SelectedApplicationMenu mn : menus){
			if(selectedMenus!=null && selectedMenus.containsKey(mn.getId())){
				mn.setSelected(true);
			}else{
				mn.setSelected(false);
			}
			
			if(mn.getFunctionIdParent()!= null){
				Long idBapak = mn.getFunctionIdParent();
				
				if ( !childMenus.containsKey(idBapak)){
					
					childMenus.put(idBapak, new ArrayList<SelectedApplicationMenu>());
				}
				childMenus.get(idBapak).add(mn);
			}else{
				rootMenus.add(mn);
			}
		}
	}
	
	public MenuTreeNode<SelectedApplicationMenu> getTreeNodes(){
		//List<SelectedApplicationMenu> menus = getMenus();
		
		loadAllMenus();
		if(rootMenus!=null && rootMenus.size() > 0){
			MenuTreeNodeCollection<SelectedApplicationMenu> menuNodes = new MenuTreeNodeCollection<SelectedApplicationMenu>();
			for (SelectedApplicationMenu menu : rootMenus) {
				MenuTreeNode<SelectedApplicationMenu> menuNode = null;
				if(menuHasChild(menu.getId())){
					menuNode = new MenuTreeNode<SelectedApplicationMenu>(menu, populateTreeNodes(getMenusByParentId(menu.getId())));
				}else{
					menuNode = new MenuTreeNode<SelectedApplicationMenu>(menu);
				}
				menuNodes.add(menuNode);
			}
			return new MenuTreeNode<SelectedApplicationMenu>(null, menuNodes);
		}else{
			return null;
		}
	}
	
	private MenuTreeNodeCollection<SelectedApplicationMenu> populateTreeNodes(List<SelectedApplicationMenu> list){
		MenuTreeNodeCollection<SelectedApplicationMenu> menuNodes = new MenuTreeNodeCollection<SelectedApplicationMenu>();
		for (SelectedApplicationMenu menu : list) {
			MenuTreeNode<SelectedApplicationMenu> menuNode = null;
			if(menuHasChild(menu.getId())){
				menuNode = new MenuTreeNode<SelectedApplicationMenu>(menu, populateTreeNodes(getMenusByParentId(menu.getId())));
			}else{
				menuNode = new MenuTreeNode<SelectedApplicationMenu>(menu);
			}
			menuNodes.add(menuNode);
		}
		return menuNodes;
	}
	
	private boolean menuHasChild(Long id){
		List<SelectedApplicationMenu> menuChild = getMenusByParentId(id);
		if(menuChild != null){
			return (menuChild.size() > 0);
		}else{
			return false;
		}
	}
	
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
			System.out.println("---Read from DB---");
			return generalPurposeDao.list(ApplicationMenuAssignment.class, filters, sortArgs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data sec_menu_assignment! ApplicationId: " + applicationId + ", Error: " + e.getMessage(), e);
			return null;
		}
	}
	
	private List<SelectedApplicationMenu> getMenus(){
		Long appId = new Long(applicationId);
		
		// Filters
		SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
			new SimpleQueryFilter("applicationId", SimpleQueryFilterOperator.equal, appId)
		};
		
		// Sorted field(s)
		SimpleSortArgument[] sortArgs = {
			new SimpleSortArgument("functionCode", true)
		};
		
		try {
			System.out.println("---Read from DB---");
			return generalPurposeDao.list(SelectedApplicationMenu.class, filters, sortArgs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data sec_menu! ApplicationId: " + applicationId + ", Error: " + e.getMessage(), e);
			return null;
		}
	}
	
}
