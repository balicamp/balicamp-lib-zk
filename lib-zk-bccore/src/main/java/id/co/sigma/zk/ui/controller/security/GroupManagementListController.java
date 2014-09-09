package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.UserGroup;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

/**
 * Group management list controller
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class GroupManagementListController extends BaseSimpleListController<UserGroup> implements IReloadablePanel{
	private static final long serialVersionUID = -4247949223409439370L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GroupManagementListController.class.getName());
	
	@Qualifier(value="securityApplicationId")
	@Autowired
	private String applicationId;
	
	@Wire private Listbox pageListBox;
	
	@QueryParameterEntry(filteredField="groupCode", queryOperator=SimpleQueryFilterOperator.likeBothSide)
	@Wire private Textbox txtGroupCode;
	
	@QueryParameterEntry(filteredField="groupName", queryOperator=SimpleQueryFilterOperator.likeBothSide)
	@Wire private Textbox txtGroupName;
	
	@Override
	public void reload() {
		invokeSearch();
	}

	@Override
	protected Class<? extends UserGroup> getHandledClass() {
		return UserGroup.class;
	}

	@Override
	public Listbox getListbox() {
		return pageListBox;
	}
	
	@Listen("onClick=#btnCari")
	public void btnCariClick(){
		invokeSearch();
	}
	
	@Listen("onClick=#btnReset")
	public void btnResetClick(){
		txtGroupCode.setValue("");
		txtGroupName.setValue("");
	}
	
	@Listen("onClick=#btnTambah")
	public void btnTambahClick(){
		UserGroup usrGrp = new UserGroup();
		EditorManager.getInstance().addNewData("~./zul/pages/master/security/GroupManagementEditor.zul", usrGrp, this);
	}
	
}
