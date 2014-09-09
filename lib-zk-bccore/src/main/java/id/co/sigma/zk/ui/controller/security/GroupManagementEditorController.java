package id.co.sigma.zk.ui.controller.security;

import java.util.ArrayList;
import java.util.List;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.common.security.domain.UserGroup;
import id.co.sigma.zk.tree.MenuTreeNode;
import id.co.sigma.zk.tree.MenuTreeNodeCollection;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.DefaultTreeModel;
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
	
	@Wire Checkbox cbSuperGroup;
	@Wire Textbox superGroup;
	
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
	
	public DefaultTreeModel<ApplicationMenu> getTreeModel(){
		return new DefaultTreeModel<ApplicationMenu>(getTreeNodes(null));
	}
	
	public MenuTreeNode<ApplicationMenu> getTreeNodes(Integer parentId){
		List<ApplicationMenu> menus = getMenus(parentId);
		if(menus!=null && menus.size() > 0){
			MenuTreeNodeCollection<ApplicationMenu> menuNodes = new MenuTreeNodeCollection<ApplicationMenu>();
			for (ApplicationMenu menu : menus) {
				MenuTreeNode<ApplicationMenu> menuNode = null;
				if(menuHasChild(menu.getId().intValue())){
					menuNode = new MenuTreeNode<ApplicationMenu>(menu, populateTreeNodes(getMenus(menu.getId().intValue())));
				}else{
					menuNode = new MenuTreeNode<ApplicationMenu>(menu);
				}
				menuNodes.add(menuNode);
			}
			return new MenuTreeNode<ApplicationMenu>(null, menuNodes);
		}else{
			return null;
		}
	}
	
	private MenuTreeNodeCollection<ApplicationMenu> populateTreeNodes(List<ApplicationMenu> menus){
		MenuTreeNodeCollection<ApplicationMenu> menuNodes = new MenuTreeNodeCollection<ApplicationMenu>();
		for (ApplicationMenu menu : menus) {
			MenuTreeNode<ApplicationMenu> menuNode = null;
			if(menuHasChild(menu.getId().intValue())){
				menuNode = new MenuTreeNode<ApplicationMenu>(menu, populateTreeNodes(getMenus(menu.getId().intValue())));
			}else{
				menuNode = new MenuTreeNode<ApplicationMenu>(menu);
			}
			menuNodes.add(menuNode);
		}
		return menuNodes;
	}
	
	private boolean menuHasChild(Integer parentId){
		List<ApplicationMenu> menuChild = getMenus(parentId);
		if(menuChild != null){
			return (menuChild.size() > 0);
		}else{
			return false;
		}
	}
	
	private List<ApplicationMenu> getMenus(Integer parentId){
		Long appId = new Long(applicationId);
		
		SimpleQueryFilter parentIdFltr = null;
		if(parentId == null){
			parentIdFltr = new SimpleQueryFilter("functionIdParent", SimpleQueryFilterOperator.fieldIsNull, parentId);
		}else{
			parentIdFltr = new SimpleQueryFilter("functionIdParent", SimpleQueryFilterOperator.equal, new Long(parentId.toString()));
		}
		SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
			new SimpleQueryFilter("applicationId", SimpleQueryFilterOperator.equal, appId),
			parentIdFltr
		};
		
		SimpleSortArgument[] sortArgs = {
			new SimpleSortArgument("functionCode", true)
		};
		
		try {
			return generalPurposeDao.list(ApplicationMenu.class, filters, sortArgs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data sec_menu! ApplicationId: " + applicationId + ", Error: " + e.getMessage(), e);
			return null;
		}
	}
	
}
