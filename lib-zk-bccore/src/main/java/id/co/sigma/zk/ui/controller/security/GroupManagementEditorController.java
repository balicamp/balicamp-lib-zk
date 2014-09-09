package id.co.sigma.zk.ui.controller.security;

import java.util.List;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.common.security.domain.UserGroup;
import id.co.sigma.zk.tree.MenuTreeNode;
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
	
	private static final SimpleSortArgument[] sortArgs = {
		new SimpleSortArgument("groupCode", true)
	};
	
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
			cbSuperGroup.setChecked(data.getSuperGroup().equalsIgnoreCase("Y"));
		}		
	}
	
	public DefaultTreeModel<ApplicationMenu> getTreeModel(){
		return new DefaultTreeModel<ApplicationMenu>(getTreeNodes());
	}
	
	public MenuTreeNode<ApplicationMenu> getTreeNodes(){
		return null;
	}
	
	private List<ApplicationMenu> getAppMenu(Integer parentId){
		Long appId = new Long(applicationId);
		
		SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
			new SimpleQueryFilter("applicationId", SimpleQueryFilterOperator.equal, appId),
			new SimpleQueryFilter("functionIdParent", SimpleQueryFilterOperator.equal, parentId)
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
