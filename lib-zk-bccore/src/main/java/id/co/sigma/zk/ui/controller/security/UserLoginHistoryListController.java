package id.co.sigma.zk.ui.controller.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.common.security.domain.Signon;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;
import id.co.sigma.zk.ui.annotations.ListOfValue;
import id.co.sigma.zk.ui.annotations.LoVSort;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

/**	
 * controller budget usage detail list
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka D.</a>
 */
public class UserLoginHistoryListController extends BaseSimpleListController<Signon>{

	@Autowired
	protected IGeneralPurposeDao dao ; 
	
	@Wire
	private Listbox listbox;
	
	@QueryParameterEntry(filteredField="user.userCode", fieldType = String.class, queryOperator = SimpleQueryFilterOperator.likeBothSide)
	@Wire
	private Textbox txtUserName; 
	
	@QueryParameterEntry(filteredField="user.realName", fieldType = String.class, queryOperator = SimpleQueryFilterOperator.likeBothSide)
	@Wire
	private Textbox txtNamaUser;
	
	/*@QueryParameterEntry(filteredField = "branchId", queryOperator = SimpleQueryFilterOperator.equal, fieldType = Long.class)*/
	@ListOfValue(codeField = "branchCode", valueField = "id", labelField = "branchName", lovClass = Branch.class, onDemand = false, sorts = { @LoVSort(field = "branchCode") })
	@Wire
	private Combobox cmbBranch;

	
	@Override
	protected Class<? extends Signon> getHandledClass() {
		return Signon.class;
	}

	@Override
	public Listbox getListbox() {
		return listbox;
	}

	
	@Override
	public void invokeSearch(final SimpleQueryFilter[] filters, final SimpleSortArgument[] sorts) {
		dataModel = new SimpleQueryDrivenListModel<Signon>() {
			
			@Override
			protected SimpleSortArgument[] getSorts() {
				return sorts;
			}
			
			@Override
			public Class<? extends Signon> getHandledClass() {
				return Signon.class;
			}
			
			@Override
			protected SimpleQueryFilter[] getFilters() {
				return filters;
			}
			
			@Override
			public List<Signon> selectFromDB(int pageSize, int firstRowPosition) {
				/*return super.selectFromDB(pageSize, firstRowPosition);*/
				List<Signon> listSignOn = new ArrayList<>();
				try {
					listSignOn = dao.list(Signon.class.getName()+" uh inner join fetch uh.user", "uh",filters, getSortArgs());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return listSignOn;
			}
		};
		
		Listbox lb = getListbox();
		dataModel.setSortArgs(getSortableFirstHeader(lb));
		dataModel.initiate(lb.getPageSize());
		dataModel.setMultiple(lb.isMultiple());
		lb.setModel(dataModel);

		if (dataModel != null) {
			if (dataModel.getHoldedData() == null
					|| dataModel.getHoldedData().isEmpty()) {

				lb.setEmptyMessage(Labels.getLabel("msg.search.empty_result"));
				lb.invalidate();
			}
		}
	}
	
	public String getBranchNameByBranchCode(String branchCode){
		SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
			new SimpleQueryFilter("branchCode", SimpleQueryFilterOperator.equal, branchCode)	
		};	
		List<Branch> branchList = null;
		try {
			branchList = dao.list(Branch.class, filters, null);
			if(branchList != null || !branchList.isEmpty()){
				return branchCode+" - "+branchList.get(0).getBranchName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "-";
	}
}
