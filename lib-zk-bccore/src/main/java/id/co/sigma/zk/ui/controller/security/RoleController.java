package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.security.domain.Role;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author <a href="mailto:rie.anggreani@gmail.com">Arie Anggreani</a>
 */
@Controller
public class RoleController extends BaseSimpleListController<Role> implements IReloadablePanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1003489155768495181L;

	@QueryParameterEntry(
			filteredField="roleCode" , 
			queryOperator=SimpleQueryFilterOperator.likeBothSide)
	@Wire
	Textbox codeSearch ;
	@QueryParameterEntry(filteredField="roleDesc" , queryOperator= SimpleQueryFilterOperator.equal)
	@Wire
	Textbox descSearch  ;
	@Wire
	Button searchButton ;
	@Wire
	Listbox listbox ; 
	@Wire Button resetButton ;
	@Wire Button addButton ; 

	@Wire Button btnEdit ; 
	@Wire Button btnHapus ;


	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		invokeSearch();
	};
	
	@Override
	protected Class<? extends Role> getHandledClass() {
		// TODO Auto-generated method stub
		return Role.class;
	}

	@Override
	public Listbox getListbox() {
		// TODO Auto-generated method stub
		return listbox;
	}

	
	@Listen(value="onClick = #resetButton")
	public void resetClick() {
		codeSearch.setValue("");
		descSearch.setValue("");
		invokeSearch();
	};
	
	@Override
	public void reload() {
		invokeSearch();
	}
	  
	@Override
	public void deleteData(Role data) {
		deleteData(data, data.getId(), "id");
		reload();
	}
	
	 @Listen(value="onClick = #addButton")
	    public void clickAdd(){
	    	Role role = new Role();
	    	EditorManager.getInstance().addNewData("~./zul/pages/master/security/RoleFormEditor.zul", role, this);
	 }
	 
	 @Listen(value="onClick = #searchButton")
	 public void click() {
	        invokeSearch();
	 }


	

	 

}
