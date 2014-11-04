package id.co.sigma.zk.ui.controller.security;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.security.domain.Role;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

/**
 * 
 * @author <a href="mailto:rie.anggreani@gmail.com">Arie Anggreani</a>
 */
public class RoleEditorController extends BaseSimpleDirectToDBEditor<Role>{

	/**
	 * 
	 */
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(RoleEditorController.class.getName());
	private static final long serialVersionUID = 7109113124969951147L;
	
	@Wire
	Textbox roleCode;
	
	
	@Listen("onClick=#btnCancel")
    public void onCancel(){
        EditorManager.getInstance().closeCurrentEditorPanel();
    }
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		if(getEditorState().equals(ZKEditorState.EDIT)){
			roleCode.setDisabled(true);
		}
	}
	
	@Override
	protected void parseEditedData(Component comp) {
		// TODO Auto-generated method stub
		super.parseEditedData(comp);
		editedData.setIsPredefined("N");
	}
	
}
