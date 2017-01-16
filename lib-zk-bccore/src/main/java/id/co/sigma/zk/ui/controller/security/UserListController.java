package id.co.sigma.zk.ui.controller.security;



import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.security.server.service.IUserService;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;
import id.co.sigma.zk.ui.annotations.ListOfValue;
import id.co.sigma.zk.ui.annotations.LoVSort;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class UserListController extends BaseSimpleListController<User> implements IReloadablePanel{

	@Autowired
	IUserService userService;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6628279768995608470L;
	@Autowired
	IGeneralPurposeService generalPurposeService ;  
	
	@Wire
	Listbox userListbox ;
	
	@QueryParameterEntry(filteredField="userCode" , 
			queryOperator=SimpleQueryFilterOperator.likeBothSide ) 
	@Wire Textbox txtUserCode;
	
	@QueryParameterEntry(filteredField="realName" , 
			queryOperator=SimpleQueryFilterOperator.likeBothSide ) 
	@Wire Textbox txtRealName;
	
	@QueryParameterEntry(filteredField = "defaultBranchCode", queryOperator = SimpleQueryFilterOperator.equal, fieldType = String.class)
	@ListOfValue(codeField = "branchCode", valueField = "branchCode", labelField = "branchName", lovClass = Branch.class, onDemand = false, sorts = { @LoVSort(field = "branchCode") })
	@Wire
	Combobox cmbBranch;
	
	@Override
	public User addNewData() {
		return new User();
	}
	/*@Wire Button btnCari ;
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
	*/
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
	public void deleteData(User data){
		try {
			userService.deleteUser(data);
			invokeSearch();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*@Override
	protected void deleteData(User data, Serializable pk, String pkFieldName) {
		// TODO Auto-generated method stub
		super.deleteData(data, pk, pkFieldName);
		try {
			userService.remove(data.getId());
			invokeSearch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	/*@Override
	public void invokeSearch(SimpleQueryFilter[] filters,
			SimpleSortArgument[] sorts) {
		// TODO Auto-generated method stub
		try {
			List<User> user = generalPurposeDao.list(User.class.getName()+" B left outer join fetch B.branch",
					"B", filters, sorts);
				getListbox().setModel(new ListModelList<>(user));
				getListbox().invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	@Override
	public void invokeSearch(final SimpleQueryFilter[] filters, final SimpleSortArgument[] sorts) {
		// TODO Auto-generated method stub
		/*super.invokeSearch(filters, sorts);*/
		dataModel = new SimpleQueryDrivenListModel<User>() {

			@Override
			public Class<? extends User> getHandledClass() {
				return User.class;
			}

			@Override
			protected SimpleQueryFilter[] getFilters() {
				return filters;
			}

			@Override
			protected SimpleSortArgument[] getSorts() {
				return sorts;
			}
			
			@Override
			public List<User> selectFromDB(int pageSize, int firstRowPosition) {
				
				List<User> retval = new ArrayList<>();
				try {
					retval = generalPurposeDao.list(User.class.getName()+" u inner join fetch u.branch ", "u", getFilters(), getSorts(), pageSize, firstRowPosition);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return retval;
			}
		};
		Listbox lb = getListbox();
		dataModel.setSortArgs(getSortableFirstHeader(lb));
		dataModel.initiate(lb.getPageSize());
		dataModel.setMultiple(lb.isMultiple());
		lb.setModel(dataModel);
		if (dataModel != null && dataModel.getHoldedData().isEmpty()) {
			lb.setEmptyMessage(Labels.getLabel("msg.search.empty_result"));
			lb.invalidate();
		}
	}
}
