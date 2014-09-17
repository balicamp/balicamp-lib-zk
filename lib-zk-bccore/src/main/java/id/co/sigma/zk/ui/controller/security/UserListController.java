package id.co.sigma.zk.ui.controller.security;



import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.security.domain.UserGroupAssignment;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class UserListController extends BaseSimpleListController<User> implements IReloadablePanel{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6628279768995608470L;
	@Autowired
	private IGeneralPurposeService generalPurposeService ;  
	
	@Wire
	Listbox userListbox ;
	
	@QueryParameterEntry(filteredField="userCode" , 
			queryOperator=SimpleQueryFilterOperator.likeBothSide ) 
	@Wire Textbox txtUserCode;
	
	@QueryParameterEntry(filteredField="realName" , 
			queryOperator=SimpleQueryFilterOperator.likeBothSide ) 
	@Wire Textbox txtRealName;
	
	
	@Wire Button btnCari ;
	@Wire Button btnReset ; 
	@Wire Button btnTambah ; 
	@Wire Button btnEdit ; 
	@Wire Button btnHapus ; 
	
	
	@Listen(value="onClick = #btnCari")
	public void searchClick() {
		invokeSearch();
	};
	
	@Listen(value="onClick = #btnReset")
	public void resetClick(){
		txtUserCode.setValue("");
		txtRealName.setValue("");
		invokeSearch();
	}
	
	@Listen(value="onClick = #btnTambah")
	public void tambahUser() {
		User usr = new User();
		EditorManager.getInstance().addNewData("~./zul/pages/master/security/UserEditor.zul", usr, this);
	}
	
	@Override
	public void reload() {
		invokeSearch();
		
	}
	@Override
	protected Class<? extends User> getHandledClass() {
		
		return User.class;
	}
	@Override
	public Listbox getListbox() {
		return userListbox;
	}
	@Override
	public void deleteData(User data) {
		System.out.println("Yg dihapus Id :");
		try {
			SimpleSortArgument[] sortArgs = {
					new SimpleSortArgument("userId", true)
				};
			if(generalPurposeDao.list(UserGroupAssignment.class, sortArgs).size()>0){
				generalPurposeService.delete(UserGroupAssignment.class, data.getId(), "userId");
			}
			generalPurposeService.delete(User.class, data.getId(), "id");
			invokeSearch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
