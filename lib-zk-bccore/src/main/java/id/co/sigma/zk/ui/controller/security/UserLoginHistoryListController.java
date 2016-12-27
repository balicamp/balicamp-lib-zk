package id.co.sigma.zk.ui.controller.security;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.common.security.domain.SigonHistoryView;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;
import id.co.sigma.zk.ui.annotations.ListOfValue;
import id.co.sigma.zk.ui.annotations.LoVSort;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;
import id.co.sigma.zk.ui.custom.component.ListOfValueItem;
import id.co.sigma.zk.ui.custom.component.ListOfValueModel;

/**	
 * controller User LOgin detail list
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka D.</a>
 */
public class UserLoginHistoryListController extends BaseSimpleListController<SigonHistoryView>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Wire
	private Listbox listbox;
	
	@QueryParameterEntry(filteredField="user.userCode", fieldType = String.class, queryOperator = SimpleQueryFilterOperator.likeBothSide)
	@Wire
	private Textbox txtUserName; 
	
	@QueryParameterEntry(filteredField="user.realName", fieldType = String.class, queryOperator = SimpleQueryFilterOperator.likeBothSide)
	@Wire
	private Textbox txtNamaUser;
	
	@QueryParameterEntry(filteredField = "user.defaultBranchCode", queryOperator = SimpleQueryFilterOperator.equal, fieldType = String.class)
	@ListOfValue(codeField = "branchCode", valueField = "branchCode", labelField = "branchName", lovClass = Branch.class, onDemand = false, sorts = { @LoVSort(field = "branchCode") })
	@Wire
	private Combobox cmbBranch;
	
	@QueryParameterEntry(filteredField = "activity", queryOperator = SimpleQueryFilterOperator.equal, fieldType = String.class)
	@Wire
	private Combobox cmbActivities;

	/*@QueryParameterEntry(filteredField = "logonTime", queryOperator = SimpleQueryFilterOperator.greaterEqual,
            fieldType = Date.class)*/
	@Wire
	private Datebox startDate;
	
	/*@QueryParameterEntry(filteredField = "logonTime", queryOperator = SimpleQueryFilterOperator.lessEqual,
            fieldType = Date.class)*/
	@Wire
	private Datebox endDate;
	
	@Override
	protected Class<? extends SigonHistoryView> getHandledClass() {
		return SigonHistoryView.class;
	}

	@Override
	public Listbox getListbox() {
		return listbox;
	}

	
	@Override
	public void invokeSearch(final SimpleQueryFilter[] filters, final SimpleSortArgument[] sorts) {
		dataModel = new SimpleQueryDrivenListModel<SigonHistoryView>() {
			
			private static final long serialVersionUID = 1L;
			

			@Override
			protected SimpleSortArgument[] getSorts() {
				return sorts;
			}
			
			@Override
			public Class<? extends SigonHistoryView> getHandledClass() {
				return SigonHistoryView.class;
			}
			
			@Override
			protected SimpleQueryFilter[] getFilters() {
				return filters;
			}
			
			@Override
			public List<SigonHistoryView> selectFromDB(int pageSize, int firstRowPosition) {
				List<SigonHistoryView> listSignOn = new ArrayList<>();
				try {
					/*listSignOn = dao.list(SigonHistoryView.class.getName()+" uh inner join fetch uh.user", "uh",filters, getSorts(), pageSize, firstRowPosition);*/
					listSignOn = generalPurposeDao.list(SigonHistoryView.class.getName()+" sh inner join fetch sh.user ", "sh", filters, getSorts(), pageSize, firstRowPosition);
					if (listSignOn != null && !listSignOn.isEmpty()) {
						/*List<Signon> listData = splitLogonLogout(listSignOn);*/
						return listSignOn;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
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
	
	/*private List<Signon> splitLogonLogout(List<Signon> listData){
		List<Signon> retval = new ArrayList<>();
		for (Signon data : listData) {
			if(data.getLogoutTime() != null){
				data.setLoginLogout(Signon.SIGNON_STATUS_LOGIN);
				retval.add(data);
				Signon dataLogout = new Signon();
				dataLogout = data;
				dataLogout.setLoginLogout(Signon.SIGNON_STATUS_LOGOUT);
				dataLogout.setLogonTime(data.getLogoutTime());
				retval.add(dataLogout);
			}else{
				data.setLoginLogout(Signon.SIGNON_STATUS_LOGIN);
				retval.add(data);
			}
		}
		return retval;
	}*/
	
	public String getBranchNameByBranchCode(String branchCode){
		if(branchCode!=""){
			SimpleQueryFilter[] filters = new SimpleQueryFilter[]{
				new SimpleQueryFilter("branchCode", SimpleQueryFilterOperator.equal, branchCode)	
			};	
			List<Branch> branchList = new ArrayList<>();
			try {
				/*branchList = dao.list(Branch.class, filters, null);*/
				branchList = generalPurposeDao.list(Branch.class, filters, null);
				if(branchList.size() > 0){
					return branchCode+" - "+branchList.get(0).getBranchName();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "-";
	}
	
	@Override
	public SimpleSortArgument[] getSorts() {
		SimpleSortArgument[] currentSort = super.getSorts();
		List<SimpleSortArgument> tmpSort = new ArrayList<>();
		if(currentSort != null && currentSort.length > 0){
			for (SimpleSortArgument sort : currentSort) {
				tmpSort.add(sort);
			}
		}
		tmpSort.add(new SimpleSortArgument("waktu", false));
		SimpleSortArgument[] finalSort = new SimpleSortArgument[tmpSort.size()];
		return tmpSort.toArray(finalSort);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cmbActivities.setModel(new ListOfValueModel(getActivities()));
	}
	
	@Override
	protected SimpleQueryFilter[] generateFilters() {
		/*// TODO Auto-generated method stub
		return super.generateFilters();*/
		/*SimpleQueryFilter[] currentFilter = super.generateFilters();
		List<SimpleQueryFilter> listFilter = new ArrayList<>();
		if(currentFilter !=null || currentFilter.length > 0){
			for (SimpleQueryFilter filter : currentFilter) {
				listFilter.add(filter);
			}
		}
		
		
		if(endDate.getValue()!=null){
			Date endDateFil = endDate.getValue();
			endDateFil.setHours(23);
			endDateFil.setMinutes(59);
			endDateFil.setSeconds(59);
			listFilter.add(new SimpleQueryFilter("logonTime", SimpleQueryFilterOperator.lessEqual, endDateFil));
		}
		
		SimpleQueryFilter[] finalFilter = new SimpleQueryFilter[listFilter.size()];
		return listFilter.toArray(finalFilter);*/
		
		SimpleQueryFilter[] currentFilter = super.generateFilters();
		List<SimpleQueryFilter> tmpFilter = new ArrayList<>();
		if(currentFilter != null && currentFilter.length > 0){
			for (SimpleQueryFilter filter : currentFilter) {
				tmpFilter.add(filter);
			}
		}
		
		if(startDate.getValue() !=null && endDate.getValue() != null){
			tmpFilter.add(new SimpleQueryFilter("waktu", startDate.getValue(), endDate.getValue()));
		}else if(startDate.getValue() !=null && endDate.getValue() == null ){
				tmpFilter.add(new SimpleQueryFilter("waktu", SimpleQueryFilterOperator.greaterEqual, startDate.getValue()));
			}else if(startDate.getValue() ==null && endDate.getValue() != null){
				tmpFilter.add(new SimpleQueryFilter("waktu", SimpleQueryFilterOperator.lessEqual, endDate.getValue()));
		}
		
		
		
		SimpleQueryFilter[] finalFilter = new SimpleQueryFilter[tmpFilter.size()];
		
		tmpFilter.toArray(finalFilter);
		return finalFilter;
	}
	
	private List<ListOfValueItem> getActivities() {
		List<String> listActivites = new ArrayList<>();
		listActivites.add("Login");
		listActivites.add("Logout");
		List<ListOfValueItem> activities = new ArrayList<>();
		/*for (int i = 0; i <= listActivites.size(); i++) {
			activities.add(new ListOfValueItem(listActivites.get(i), listActivites.get(i), ""));
		}*/
		
		for (String data : listActivites) {
			activities.add(new ListOfValueItem(data.toString(), data.toString(), ""));
		}
		return activities;
	}
	
	@Override
	protected void resetFilter() {
		super.resetFilter();
		endDate.setValue(null);
		startDate.setValue(null);
	}
}
