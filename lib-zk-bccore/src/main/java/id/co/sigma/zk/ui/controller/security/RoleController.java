package id.co.sigma.zk.ui.controller.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.security.domain.ApplicationMenu;
import id.co.sigma.common.security.domain.ApplicationMenuAssignment;
import id.co.sigma.common.security.domain.Role;
import id.co.sigma.common.security.domain.UserRole;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zkoss.zhtml.Messagebox;
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
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(ApplicationMenuListController.class.getName());
	
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

	@Autowired
	private IGeneralPurposeService generalPurposeService ; 
	private List<Role> listRole;
	
	
	public List<Role> getListRole() {
		return listRole;
	}

	public void setListRole(List<Role> listRole) {
		this.listRole = listRole;
	}

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

	
	@Override
	public Role addNewData() {
		return new Role();
	}

	@Override
	public void reload() {
		invokeSearch();
	}
	
	
	/**
	 * get all user role
	 * @param keys
	 * @return
	 */
	protected List<UserRole> getListUserRole(List<Serializable> keys){
		try {
			return generalPurposeDao.loadDataByKeys(UserRole.class, "roleId", keys);
		} catch (Exception e) {
			logger.error("Gagal get data Role,"+e.getMessage(),e);
			return null;
		}
	}
	  
	@Override
	public void deleteData(Role data) {
		List<Serializable> listId = null;
		listId=new ArrayList<>();
		listId.add(data.getId());
		List<UserRole> userRole = getListUserRole(listId);
		if(userRole!=null && !userRole.isEmpty()){
			Messagebox.show("Gagal hapus data role, role digunakan di user lain", "Hapus Role", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		try {
			deleteData(data, data.getId(), "id");
			reload();
		} catch (Exception e) {
			logger.error("Gagal baca Role,"+e.getMessage(),e);
	
		}
		
	}
	
}
