package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;

public class ActionButton extends Div implements IdSpace, AfterCompose {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Integer EDIT_BUTTON = 0;
	public static final Integer DELETE_BUTTON = 1;
	
	private BaseSimpleController controller;
	
	private String editEvent;
	private String deleteEvent;
	
	private String targetPath;
	private String editorPage;
	
	private String deleteMsg;
	
	private boolean delete = true;
	private boolean edit = true;
	
	private int childIndex = 0;
	
	public ActionButton() {
		Executions.createComponents("~./zul/pages/common/ActionButton.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);		
	}

	public BaseSimpleController getController() {
		return controller;
	}

	public void setController(BaseSimpleController controller) {
		this.controller = controller;
	}

	@Override
	public void afterCompose() {
		
		List<Component> children = getChildren();
		Component editComp = children.get(EDIT_BUTTON);
		Component delComp = children.get(DELETE_BUTTON);
		editComp.setVisible(edit);
		delComp.setVisible(delete);
		
		editComp.addEventListener("onClick", new EventListener<Event>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void onEvent(Event event) throws Exception {
				
				Component comp = getParent(event.getTarget());
				
				SingleKeyEntityData<Serializable> data = null;
				ZKClientSideListDataEditorContainer container = null;
				
				if(comp instanceof Listitem) {
					Listitem item = (Listitem)comp;
					data =  item.getValue();
					ListModel<Object> model = item.getListbox().getModel();
					if(model instanceof ZKClientSideListDataEditorContainer) {
						container = (ZKClientSideListDataEditorContainer)model;
					}
				} else if(comp instanceof Row) {
					data = ((Row)comp).getValue();
					ListModel<Object> model = ((Row)comp).getGrid().getModel();
					if(model instanceof ZKClientSideListDataEditorContainer) {
						container = (ZKClientSideListDataEditorContainer)model;
					}
				}
				
				if(data != null) {
					if(controller instanceof BaseSimpleListController) {
						EditorManager.getInstance().editData(editorPage, data, controller);
					} else if(controller instanceof BaseSimpleDirectToDBEditor) {
						EditorManager.getInstance().editData(editorPage, container, data, controller);
					}
				}
			}
			
		});
		
		delComp.addEventListener("onClick", new EventListener<Event>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onEvent(Event event) throws Exception {
				
				Component comp = getParent(event.getTarget());
				
				Object o = null;
				ZKClientSideListDataEditorContainer<Object> container = null;
				
				if(comp instanceof Listitem) {	//listbox			
					Listitem item = (Listitem)comp;					
					o = item.getValue();			

					ListModel<Object> model = item.getListbox().getModel();
					if(model instanceof ZKClientSideListDataEditorContainer) {
						container = (ZKClientSideListDataEditorContainer<Object>)model;
					}
					
				} else if(comp instanceof Row) { //grid
					Row row = (Row)comp;
					o = row.getValue();
					ListModel<Object> model = ((Row)comp).getGrid().getModel();
					if(model instanceof ZKClientSideListDataEditorContainer) {
						container = (ZKClientSideListDataEditorContainer<Object>)model;
					}					
				}
				
				if(o != null) {
					final Object data =  o;
					final ZKClientSideListDataEditorContainer<Object> cntr = container;
					if(deleteMsg == null || deleteMsg.trim().length() == 0) {
						deleteMsg = "Apakah anda yakin akan menghapus data ?";
					}
					
					Messagebox.show(deleteMsg, "Delete Confirmation", Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
	
						@SuppressWarnings({ "unchecked"})
						@Override
						public void onEvent(Event event) throws Exception {
							switch(((Integer)event.getData()).intValue()) {
							case Messagebox.YES:
								if(controller instanceof BaseSimpleListController) {
									((BaseSimpleListController<Serializable>)controller).deleteData((SingleKeyEntityData<?>)data);
								} else if(controller instanceof BaseSimpleDirectToDBEditor) {
									((BaseSimpleDirectToDBEditor)controller).deleteChildData(data, cntr);
								}
								break;
							case Messagebox.NO:
								break;
							}
						}
						
					});
					
				}
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

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = Boolean.valueOf(delete);
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(String edit) {		
		this.edit = Boolean.valueOf(edit);
	}

	public int getChildIndex() {
		return childIndex;
	}

	public void setChildIndex(String childIndex) {
		this.childIndex = Integer.valueOf(childIndex);
	}
	
	private Component getParent(Component comp) {
		Component prnt = comp;
		while(!((prnt instanceof Listitem) || (prnt instanceof Row))) {
			prnt = prnt.getParent();
		}
		return prnt;
	}
}
