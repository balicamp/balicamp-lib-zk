package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.security.domain.UserGroup;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka Darmawan</a>
 */
public class UserEditorController extends BaseSimpleDirectToDBEditor<User>{
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(UserEditorController.class.getName());
	
	
	@Override
	public void insertData() throws Exception {
		logger.info("Set application id");
		getEditedData().setDefaultApplicationId(1);
		super.insertData();
	}
	
	@Wire
	private Bandbox bdBranch;
	
	@Wire
	Listbox listBoxBandBox;
	
	private ListModel<UserGroup> listUserGroup;
	
	public ListModel<UserGroup> getListUserGroup() {
		return listUserGroup;
	}
	
	@Wire
	Checkbox listBoxCheckList;
	
	private List<Branch> listBranchObject;

	
	public List<Branch> getListBranchObject() {
		return listBranchObject;
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		listBranchObject = new ArrayList<Branch>();
		
		listBranchObject = getDataBranch();
		if(listBranchObject!=null){
			listBoxBandBox.setModel(new ListModelList<Branch>(listBranchObject));
		}
		
		listUserGroup = new ListModelList<UserGroup>(getUserGroupList());
	}
	
	private List<Branch> getDataBranch(){
		List<Branch> dataListBranch = new ArrayList<Branch>();
		SimpleSortArgument[] sortArgs = {
			new SimpleSortArgument("branchCode", true)
		};
		try {
			dataListBranch = generalPurposeDao.list(Branch.class, sortArgs);
			System.out.println("jumlah List Branch : "+dataListBranch.size());
			return dataListBranch;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data sec_branch!, Error: " + e.getMessage(), e);
			return null;
		}
	}
	
	private List<UserGroup> getUserGroupList(){
		List<UserGroup> listUserGroup = new ArrayList<UserGroup>();
		SimpleSortArgument[] sortArgs = {
				new SimpleSortArgument("groupCode", true)
		};
		try {
			listUserGroup = generalPurposeDao.list(UserGroup.class, sortArgs);
			return listUserGroup;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Gagal membaca data Sec_group!, Error: " + e.getMessage(), e);
			return null;
		}
	}
	
}
