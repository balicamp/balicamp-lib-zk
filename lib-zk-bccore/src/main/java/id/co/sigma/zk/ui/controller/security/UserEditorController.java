package id.co.sigma.zk.ui.controller.security;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.web.servlet.dsp.action.ForEach;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;

import id.co.sigma.common.security.domain.User;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

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
	
	
	
	private List<String[]> listBranch = getListBranchTmp();

	
	public List<String[]> getListBranch() {
		return listBranch;
	}

	private List<String[]> getListBranchTmp(){
		
		List<String[]> listBranchTmp = new ArrayList<String[]>();
		for (int i = 0; i < 5; i++) {
			listBranchTmp.add(getArrayBranch(i));
		}
		return listBranchTmp;
	}
	
	private String[] getArrayBranch(int index){
		String[] branch = new String[]{
				"KDCB-"+index,
				"Kantor Cabang-"+index
		};
		return branch;
	}
	
	
}
