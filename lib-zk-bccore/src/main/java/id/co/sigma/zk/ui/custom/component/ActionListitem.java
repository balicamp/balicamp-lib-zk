package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Listitem;

public class ActionListitem extends Listitem implements IdSpace, AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String editorPage;
	
	private String deleteMsg;
	
	private BaseSimpleController controller;
	
	private boolean deletable = true;
	
	private boolean editable = true;
	
	private int childIndex = 0;
	
	public ActionListitem() {
		Executions.createComponents("~./zul/pages/common/ActionListitem.zul", this, null);
		
		Selectors.wireComponents(this, this, false);
		Selectors.wireVariables(this, this, null);
		Selectors.wireEventListeners(this, this);
	}

	@Override
	public void afterCompose() {
		List<Component> children = getChildren();
		Component[] dynamics = new Component[children.size()-2];
		Component[] defaults = new Component[2];
		Component[] alls = children.toArray(new Component[children.size()]);
		
		System.arraycopy(alls, 0, defaults, 0, 2);
		System.arraycopy(alls, 2, dynamics, 0, children.size()-2);
		children.clear();
		
		defaults[1].setId(defaults[1].getUuid());
		
		for(Component cmp : dynamics) {
			ActionUtils.registerClientEventListner(cmp.getChildren(), defaults[1].getUuid());
			children.add(cmp);
		}
		for(Component cmp : defaults) {
			if(cmp.getChildren().size() > 0) {
				if(cmp.getChildren().get(0) instanceof ActionButton) {
					initActionButton((ActionButton)cmp.getChildren().get(0));
				}
			}
			children.add(cmp);
		}
	}

	public String getEditorPage() {
		return editorPage;
	}

	public void setEditorPage(String editorPage) {
		this.editorPage = editorPage;
	}

	public String getDeleteMsg() {
		return deleteMsg;
	}

	public void setDeleteMsg(String deleteMsg) {
		this.deleteMsg = deleteMsg;
	}

	public BaseSimpleController getController() {
		return controller;
	}

	public void setController(BaseSimpleController controller) {
		this.controller = controller;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(String deletable) {
		this.deletable = Boolean.valueOf(deletable);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(String editable) {
		this.editable = Boolean.valueOf(editable);
	}

	
	public int getChildIndex() {
		return childIndex;
	}

	public void setChildIndex(String childIndex) {
		this.childIndex = Integer.valueOf(childIndex);
	}

	/**
	 * init action button
	 * @param actBtn
	 */
	private void initActionButton(ActionButton actBtn) {
		actBtn.setEdit(Boolean.toString(editable));
		actBtn.setDelete(Boolean.toString(deletable));
		actBtn.setController(controller);
		actBtn.setDeleteMsg(deleteMsg);
		actBtn.setEditorPage(editorPage);
		actBtn.getChildren().get(ActionButton.EDIT_BUTTON).setVisible(editable);
		actBtn.getChildren().get(ActionButton.DELETE_BUTTON).setVisible(deletable);
	}
}
