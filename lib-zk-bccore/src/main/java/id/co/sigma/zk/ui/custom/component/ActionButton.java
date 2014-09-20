package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import java.io.Serializable;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

public class ActionButton extends Div implements IdSpace, AfterCompose {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Integer EDIT_BUTTON = 0;
	public static final Integer DELETE_BUTTON = 1;
	
	private BaseSimpleListController<Serializable> controller;
	
	private String editEvent;
	private String deleteEvent;
	
	private String targetPath;
	private String editorPage;
	
	private String deleteMsg;
	
	public ActionButton() {
		Executions.createComponents("~./zul/pages/common/ActionButton.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);		
	}

	public BaseSimpleListController<Serializable> getController() {
		return controller;
	}

	public void setController(BaseSimpleListController<Serializable> controller) {
		this.controller = controller;
	}

	@Override
	public void afterCompose() {
		List<Component> children = getChildren();
		Component editComp = children.get(EDIT_BUTTON);
		Component delComp = children.get(DELETE_BUTTON);
		
		editComp.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				
				Component comp = event.getTarget().getParent().getParent().getParent();
				Listitem item = (Listitem)comp;
				SingleKeyEntityData<Serializable> data =  item.getValue();
				
				EditorManager.getInstance().editData(editorPage, data, controller);
			}
			
		});
		
		delComp.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				
				Component comp = event.getTarget().getParent().getParent().getParent();
				Listitem item = (Listitem)comp;
				final SingleKeyEntityData<Serializable> data =  item.getValue();
				
				if(deleteMsg == null || deleteMsg.trim().length() == 0) {
					deleteMsg = "Apakah anda yakin akan menghapus data ?";
				}
				
				Messagebox.show(deleteMsg, "Delete Confirmation", Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						switch(((Integer)event.getData()).intValue()) {
						case Messagebox.YES:
							controller.deleteData(data);
							break;
						case Messagebox.NO:
							break;
						}
					}
					
				});
				
			}
			
		});
		
	}

	public String getEditEvent() {
		return editEvent;
	}

	public void setEditEvent(String editEvent) {
		this.editEvent = editEvent;
	}

	public String getDeleteEvent() {
		return deleteEvent;
	}

	public void setDeleteEvent(String deleteEvent) {
		this.deleteEvent = deleteEvent;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
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
	
	
}
