package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class ActionListitem extends Listitem implements IdSpace, AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String editorPage;
	
	private String viewPage;
	
	private String deleteMsg;
	
	private BaseSimpleController controller;
	
	private boolean deletable = true;
	
	private boolean editable = true;
	
	private boolean viewable = false;
	
	private boolean modal = false;
	
	private int childIndex = 0;
	
	private boolean child = false;
	
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
		
		if(isChild()) {
			ListModel<Object> model = getListbox().getModel();
			int existing = 1; //0: new, 1: existing, 2: edited
			if(model instanceof ZKClientSideListDataEditorContainer) {
				ZKClientSideListDataEditorContainer<Object> cModel = (ZKClientSideListDataEditorContainer<Object>)model;
				Object data = getValue();
				existing = cModel.isNewObject(data) ? 0 : 1;
				if(existing == 1 && cModel.getEditedData() != null) {
					existing = cModel.getEditedData().contains(data) ? 2 : existing;
				}
			}
			
//			((Listcell)defaults[1]).setLabel(existing == 1 ? "": (existing == 2 ? "*" : "+"));
			((Listcell)defaults[1]).setIconSclass(existing == 1 ? "": (existing == 2 ? "z-icon-pencil" : "z-icon-asterisk"));
		}
		
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

	public boolean isChild() {
		return child;
	}

	public void setChild(String child) {
		this.child = Boolean.valueOf(child);
	}

	/**
	 * @return the modal
	 */
	public boolean isModal() {
		return modal;
	}

	/**
	 * @param modal the modal to set
	 */
	public void setModal(String modal) {
		this.modal = Boolean.valueOf(modal);
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
		actBtn.setViewPage(viewPage);
		actBtn.setModal(modal);
		actBtn.getChildren().get(ActionButton.VIEW_BUTTON).setVisible(viewable);
		actBtn.getChildren().get(ActionButton.EDIT_BUTTON).setVisible(editable);
		actBtn.getChildren().get(ActionButton.DELETE_BUTTON).setVisible(deletable);
		actBtn.setChildIndex(String.valueOf(childIndex));
	}

	public boolean isViewable() {
	    return viewable;
	}

	public void setViewable(boolean viewable) {
	    this.viewable = viewable;
	}

	public String getViewPage() {
	    return viewPage;
	}

	public void setViewPage(String viewPage) {
	    this.viewPage = viewPage;
	}
}
