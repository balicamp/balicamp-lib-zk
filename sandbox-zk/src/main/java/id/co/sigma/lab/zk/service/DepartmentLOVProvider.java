package id.co.sigma.lab.zk.service;

import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.sandbox.shared.domain.Department;
import id.co.sigma.zk.ui.lov.ZKGenericCustomLOVProvider;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class DepartmentLOVProvider extends ZKGenericCustomLOVProvider<String, Department>{

	
	
	static final SimpleSortArgument[] SORT_ARGS = new SimpleSortArgument[]{
		new SimpleSortArgument("code" , true)
	}; 
	@Override
	protected String getModulCode() {
		return "SANDBOX";
	}

	@Override
	protected String getParameterId() {
		return "DEPT";
	}

	@Override
	protected String getVersionFromData(Department data) {
		return null;
	}

	@Override
	protected boolean isNewerVersionNumber(String currentVersion,
			String scannedVersion) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected Class<Department> getLOVSourceClass() {
		return Department.class;
	}

	@Override
	protected SimpleSortArgument[] getLOVSorter() {
		return SORT_ARGS;
	}

	@Override
	protected CommonLOV translateToCommonLovData(Department data) {
		CommonLOV c = new CommonLOV(); 
		c.setLabel(data.getName() + "[" + data.getNumberOfPeoples() +"]");
		c.setDataValue(data.getCode());
		return c;
	}

}
