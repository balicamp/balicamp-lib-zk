package id.co.sigma.zk.ui.controller.security;

import id.co.sigma.common.security.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

/**
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka Darmawan</a>
 */
public class UserGroupListController extends SelectorComposer<Component>{
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(UserGroupListController.class.getName());
	
	private List<User> listUser;
	private ListModel<String> selectedCheckboxs = new ListModelList<String>(getUserGroup());
	
	
	public static List<String> getUserGroup() {
        return Arrays.asList(new String[]{"Adam", "Brian", "Cary", "Danny", "Edward", "Franklin", "Geroge"});
    }
	
	@Wire
	private Checkbox clinicalSitesCheckbox;
          
	
	

	public ListModel<String> getSelectedCheckboxs() {
		return selectedCheckboxs;
	}

	private User getDataUser(int dataKe){
		User data = new User();
		data.setUserCode("EK-"+dataKe);
		data.setEmail("Email-"+dataKe);
		Messagebox.show("get data : "+dataKe);
		return data;
	}
	
}
