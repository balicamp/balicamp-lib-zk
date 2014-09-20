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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

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
	
	@Wire Checkbox cbActiveFlag;
	@Wire Textbox activeFlag;
	
	@Wire Tree groupMgmTree;
	
	// ini untuk menampung semua menu yg dicentang (checked)
	private Map<Long, SelectedApplicationMenu> checkedMenus = new HashMap<>(); 
	
	// ini untuk menampung menu parent yg harus di-uncheck setelah tidak ada anak2nya yg dipilih
	private List<String> parentCodesToClear = new ArrayList<String>();
	
	// semua data menu yg akan digunakan untuk keperluan ketika menu parent dicentang maka akan
	// mencentang semua anak2nya
	private List<String> childMenusToBeChecked = new ArrayList<String>();
	
	private void removeFromCheckedMenus(SelectedApplicationMenu menu){
		if(!parentCodesToClear.isEmpty()){
			parentCodesToClear.clear();
		}
		
		if(checkedMenus.containsKey(menu.getId())){
			checkedMenus.remove(menu.getId());
		}
		
		// Removing parent if all children are unchecked
		String menuTreeCode =  menu.getMenuTreeCode() ;
		if(menuTreeCode!=null && menuTreeCode.contains(".")){
			String parentCode = getParentTreeCodes(menuTreeCode); 
			int nChild = 0;
			for(SelectedApplicationMenu scn : checkedMenus.values()){
				String scnMenuTreeCode = scn.getMenuTreeCode();
				if( (scnMenuTreeCode.length() > 1) && 
					scnMenuTreeCode.startsWith(parentCode) &&
					!scnMenuTreeCode.equals(parentCode)
				){
					nChild++;
				}
			}
			
			if(nChild == 0){
				parentCodesToClear.add(parentCode);
				checkedMenus.remove(menu.getFunctionIdParent());
				refreshCheckboxesOnTree(true);
			}
		}
		
		//TODO Jika yg di-uncheck adalah parent dan semua anak2nya checked, maka semua childnya harus ikut di-uncheck
	}
	
	private void addToCheckedMenus(SelectedApplicationMenu menu){
		if(!childMenusToBeChecked.isEmpty()){
			childMenusToBeChecked.clear();
		}
		
		if(!checkedMenus.containsKey(menu.getId())){
			checkedMenus.put(menu.getId(), menu);
		}
		
		// if current checked menu has children then add those children also
		if(menuHasChild(menu.getId())){
			String parentMenuTreeCode =  menu.getMenuTreeCode();
			Collection<Treeitem> treeItems = getTreeItems();
			for (Treeitem treeitem : treeItems) {
				SelectedApplicationMenu sam = extractMenuDataFromTreeItem(treeitem);
				String menuTreeCode = sam.getMenuTreeCode();
				if(	menuTreeCode!=null && (menuTreeCode.length()>1) && 
					parentMenuTreeCode!=null &&
					menuTreeCode.startsWith(parentMenuTreeCode) &&
					!menuTreeCode.equals(parentMenuTreeCode)
				){
					childMenusToBeChecked.add(menuTreeCode);
					checkedMenus.put(sam.getId(), sam);
				}
			}
			
			if(!childMenusToBeChecked.isEmpty()){
				refreshCheckboxesOnTree(false);
			}
		}
		
		//TODO Jika yg di-checked memiliki parent dan semua anak2nya sudah checked, maka parentnya harus ikut di-checked
	}
	
	private Collection<Treeitem> getTreeItems(){
		return groupMgmTree.getItems();
	}
	
	private SelectedApplicationMenu extractMenuDataFromTreeItem(Treeitem ti){
		MenuTreeNode<SelectedApplicationMenu> node = ti.getValue();
		SelectedApplicationMenu sam = node.getData();
		return sam;
	}
	
	private void refreshCheckboxesOnTree(boolean isUnchecking){
		Collection<Treeitem> treeItems = getTreeItems();
		
		for(Treeitem treeItem : treeItems){
			SelectedApplicationMenu sam = extractMenuDataFromTreeItem(treeItem);
			Treerow row = treeItem.getTreerow();
			Checkbox cb = (Checkbox) row.getFirstChild().getFirstChild();
			
			if(isUnchecking){
				if(parentCodesToClear.contains(sam.getMenuTreeCode())){
					cb.setChecked(false);
					System.out.println("Parent CB with menuTreeCode = " + sam.getMenuTreeCode() + " is unchecked");
				}
			}else{
				if(childMenusToBeChecked.contains(sam.getMenuTreeCode())){
					cb.setChecked(true);
					System.out.println("Child CB with menuTreeCode = " + sam.getMenuTreeCode() + " is checked");
				}
			}
		}
	}
	
	private String getParentTreeCodes(String menuTreeCode){
		int lastDotPos = menuTreeCode.lastIndexOf(".");
		int endIndex = ((lastDotPos-1)>0)? lastDotPos : 1;
		String retval = menuTreeCode.substring(0, endIndex);
		return retval;
	}
	
	@Override
	protected void insertData(UserGroup data) throws Exception {
		try {
			getEditedData().setApplicationId(new Long(applicationId));
			if(getEditedData().getSuperGroup().isEmpty()){
				getEditedData().setSuperGroup("N");
			}
			if(getEditedData().getActiveFlag().isEmpty()){
				getEditedData().setActiveFlag("A");
			}
			
			super.insertData(data);
			saveMenuAssignment(data.getId()); // Menurut teori, setelah data berhasil disimpan maka id (auto increment) sudah terisi :)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void updateData(UserGroup data) throws Exception {
		Long groupId = getEditedData().getId();
		
		try {
			super.updateData(data);
			saveMenuAssignment(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveMenuAssignment(Long groupId){
		try {
			// delete all menu assignments data by groupId
			List<ApplicationMenuAssignment> currAssigns = getMenuAssignments(groupId);
			if(!currAssigns.isEmpty()){
				for (ApplicationMenuAssignment scn : currAssigns) {
					generalPurposeService.delete(scn);
				}
			}
			
			// insert new menu assignments
			if(checkedMenus.size() > 0){
				for (SelectedApplicationMenu mnu : checkedMenus.values()) {
					ApplicationMenuAssignment assign = new ApplicationMenuAssignment();
					assign.setFunctionId(mnu.getId());
					assign.setGroupId(groupId);
					assign.setCreatedBy("GSR"); // << Nanti harus diisi dengan user yg login
					assign.setCreatedOn(new Date());
					
					generalPurposeService.insert(assign);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Listen("onCbTreeClick=#groupMgmTree")
	public void onCbTreeClick(ForwardEvent fe){
		Checkbox cb = (Checkbox) fe.getOrigin().getTarget();
		Treeitem ti = (Treeitem) cb.getParent().getParent().getParent();
		MenuTreeNode<SelectedApplicationMenu> node = ti.getValue();
		SelectedApplicationMenu sam = node.getData();
		
		if(cb.isChecked()){
			addToCheckedMenus(sam);
		}else{
			removeFromCheckedMenus(sam);
		}
	}
	
	@Listen("onCheck=#cbSuperGroup")
	public void onSuperGroupChecked(){
		String isSuperGroup = cbSuperGroup.isChecked()? "Y" : "N";
		superGroup.setValue(isSuperGroup);
	}
	
	@Listen("onCheck=#cbActiveFlag")
	public void onActiveFlagChecked(){
		String isActive = cbActiveFlag.isChecked()? "A" : "D";
		activeFlag.setValue(isActive);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		UserGroup data = getEditedData();
		if(data != null){
			boolean checked =  "Y".equalsIgnoreCase(data.getSuperGroup());
			cbSuperGroup.setChecked(checked);
			
			boolean isActive = "A".equalsIgnoreCase(data.getActiveFlag());
			cbActiveFlag.setChecked(isActive);
		}		
	}
	
	// BEGIN functions for building tree
	
	public DefaultTreeModel<SelectedApplicationMenu> getTreeModel(){
		return new DefaultTreeModel<SelectedApplicationMenu>(getTreeNodes());
	}
	
	private List<SelectedApplicationMenu> rootMenus = new ArrayList<SelectedApplicationMenu>();
	private Map<Long, List<SelectedApplicationMenu>> childMenus  = new HashMap<>();
	private Map<Long, ApplicationMenu> selectedMenus = new HashMap<>();
	
	private List<SelectedApplicationMenu> getMenusByParentId (Long idParent ){
		return childMenus.get(idParent); 
	}
	
	private void loadMenuData() {
		selectedMenus.clear();
		if(getEditedData()!=null && getEditedData().getId()!=null){
			List<ApplicationMenuAssignment> menuAss = getMenuAssignments(getEditedData().getId());
			for (ApplicationMenuAssignment menuAs : menuAss) {
				selectedMenus.put(menuAs.getFunctionId(), menuAs.getFunction());
			}
		}
		
		checkedMenus.clear();
		List<SelectedApplicationMenu> menus = getMenus(getEditedData().getId());
		for(SelectedApplicationMenu mn : menus){
			if(selectedMenus!=null && selectedMenus.containsKey(mn.getId())){
				mn.setSelected(true);
				checkedMenus.put(mn.getId(), mn);
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
		
		loadMenuData();
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
			System.out.println("---Read from DB---");
			return generalPurposeDao.list(ApplicationMenuAssignment.class, filters, sortArgs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data sec_menu_assignment! ApplicationId: " + applicationId + ", Error: " + e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Get menu data by group ID
	 * @param groupId - Group ID
	 * @return
	 */
	private List<SelectedApplicationMenu> getMenus(Long groupId ){
		Long appId = new Long(applicationId);
		
		// Filters
		SimpleQueryFilter[] filtersMenus = new SimpleQueryFilter[]{
			new SimpleQueryFilter("applicationId", SimpleQueryFilterOperator.equal, appId)
		};
		SimpleQueryFilter[] filtersAssignMenus = null;
		if(groupId!=null){
			filtersAssignMenus = new SimpleQueryFilter[]{
				new SimpleQueryFilter("groupId", SimpleQueryFilterOperator.equal, groupId)
			};
		}
		
		// Sorted field(s)
		SimpleSortArgument[] sortArgs = {
			new SimpleSortArgument("functionCode", true)
		};
		
		try {
			System.out.println("---Read from DB---");
			List<ApplicationMenu> menuAll =generalPurposeDao.list(ApplicationMenu.class, filtersMenus, sortArgs); 
			if ( menuAll== null || menuAll.isEmpty()){
				return null;
			}
			ArrayList<SelectedApplicationMenu> menuClone  = new ArrayList<>();
			
			Map<Long, SelectedApplicationMenu> indexedMenus = new HashMap<>(); 
			for ( ApplicationMenu scn : menuAll) {
				SelectedApplicationMenu  swap = new SelectedApplicationMenu(scn); 
				menuClone.add(swap);
				indexedMenus.put(swap.getId(), swap) ; 
			}
			
			if(filtersAssignMenus==null){
				return menuClone;
			}
			
			// Get menu assigment data and mark indexedMenus as selected
			List<ApplicationMenuAssignment> assgs = generalPurposeDao.list(ApplicationMenuAssignment.class, filtersAssignMenus, null); 
			if ( assgs!= null && !assgs.isEmpty()) {
				for (ApplicationMenuAssignment  scn : assgs ) {
					if ( indexedMenus.containsKey(scn.getFunctionId())){
						indexedMenus.get(scn.getFunctionId()).setSelected(true); 
					}
				}
			}
			
			return menuClone ; 
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data sec_menu! ApplicationId: " + applicationId + ", Error: " + e.getMessage(), e);
			return null;
		}
	}
	
	// END functions for building tree
}
