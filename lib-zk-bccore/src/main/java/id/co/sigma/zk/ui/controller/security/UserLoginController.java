package id.co.sigma.zk.ui.controller.security;

import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.common.security.domain.Signon;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

public class UserLoginController extends BaseSimpleListController<Signon> {

	SimpleQueryDrivenListModel<Signon> dataModel;
	private static final long serialVersionUID = -6839141891402028043L;

	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserLoginController.class.getName());
	@Wire
	private Listbox lstUser;

	@Override
	public Listbox getListbox() {
		return lstUser;
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		System.out.println("test 123s");
		invokeSearch();
	}

	public String getBranchNameByBranchCode(String branchCode) {
		SimpleQueryFilter[] filters = new SimpleQueryFilter[] {
				new SimpleQueryFilter("branchCode", SimpleQueryFilterOperator.equal, branchCode) };
		List<Branch> branchList = null;
		try {
			branchList = generalPurposeDao.list(Branch.class, filters, null);
			if (branchList != null || !branchList.isEmpty()) {
				return branchCode + " - " + branchList.get(0).getBranchName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "-";
	}

	@SuppressWarnings("serial")
	@Override
	protected SimpleQueryDrivenListModel<Signon> instantiateDataModel(final SimpleQueryFilter[] filters,
			final SimpleSortArgument[] sorts) {
		// TODO Auto-generated method stub
		return new SimpleQueryDrivenListModel<Signon>() {

			@Override
			public Class<? extends Signon> getHandledClass() {
				// TODO Auto-generated method stub
				return Signon.class;
			}

			@Override
			protected SimpleQueryFilter[] getFilters() {
				// TODO Auto-generated method stub
				return filters;
			}

			@Override
			protected SimpleSortArgument[] getSorts() {
				// TODO Auto-generated method stub
				return sorts;
			}

			@Override
			public List<Signon> selectFromDB(int pageSize, int firstRowPosition) {
				// TODO Auto-generated method stub
				try {

					// return generalPurposeDao.list(Signon.class, getFilters(),
					// getSorts(), pageSize, firstRowPosition);
					return generalPurposeDao.list(Signon.class.getName() + " M inner join fetch M.user", "M",
							getFilters(), getSorts(), pageSize, firstRowPosition);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
	}

	@Override
	protected Class<? extends Signon> getHandledClass() {
		return Signon.class;
	}

}
