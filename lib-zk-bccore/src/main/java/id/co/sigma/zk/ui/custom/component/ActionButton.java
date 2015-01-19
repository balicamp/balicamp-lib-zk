package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.io.Serializable;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Span;

public class ActionButton extends Div implements IdSpace, AfterCompose {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Integer VIEW_BUTTON = 0;
	public static final Integer EDIT_BUTTON = 1;
	public static final Integer DELETE_BUTTON = 2;
	
	private BaseSimpleController controller;
	
	private String editEvent;
	private String deleteEvent;
	
	private String targetPath;
	private String editorPage;
	private String viewPage;
	
	private String deleteMsg;
	
	private boolean view = false;
	private boolean delete = true;
	private boolean edit = true;
	private boolean modal = false;
	
	private int childIndex = 0;
	
	@Wire private Span btnView;
	
	@Wire private Span btnEdit;
	
	@Wire private Span btnDelete;
	
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
		//Component viewComp = children.get(VIEW_BUTTON);
		Component viewComp = btnView;
		//Component editComp = children.get(EDIT_BUTTON);
		Component editComp = btnEdit;
		//Component delComp = children.get(DELETE_BUTTON);
		Component delComp = btnDelete;
		viewComp.setVisible(view);
		editComp.setVisible(edit);
		delComp.setVisible(delete);
		
		/*
		 * Event listener untuk tombol view
		 */
		viewComp.addEventListener("onClick", new EventListener<Event>() {

		    @SuppressWarnings({ "rawtypes", "unused" })
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
				EditorManager.getInstance().viewData(viewPage, data, controller, modal);
			    }
			}
		    }
		    
		});
		
		/*
		 * Event listener untuk tombol edit
		 */
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
						EditorManager.getInstance().editData(editorPage, data, controller, modal);
					} else if(controller instanceof BaseSimpleDirectToDBEditor) {
						EditorManager.getInstance().editData(editorPage, container, data, controller, modal);
					}
				}
			}
			
		});
		
		/*
		 * Event listener untuk tombol delete
		 */
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
						deleteMsg = "Apakah anda yakin akan menghapus data?";
					}
					
					Messagebox.show(deleteMsg, Labels.getLabel("title.msgbox.confirmation"), 
							new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO},
							new String[]{Labels.getLabel("action.button.yes"), Labels.getLabel("action.button.no")},
							Messagebox.QUESTION, 
							Messagebox.Button.YES,
							new EventListener<Messagebox.ClickEvent>() {
	
						@SuppressWarnings({ "unchecked"})
						@Override
						public void onEvent(Messagebox.ClickEvent event) throws Exception {
							if(Messagebox.Button.YES.equals(event.getButton())){
								if(controller instanceof BaseSimpleListController) {
									((BaseSimpleListController<Serializable>)controller).deleteData((SingleKeyEntityData<?>)data);
								} else if(controller instanceof BaseSimpleDirectToDBEditor) {
									((BaseSimpleDirectToDBEditor)controller).deleteChildData(data, cntr);
								}
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
	
	/**
	 * @return the modal
	 */
	public boolean isModal() {
		return modal;
	}

	/**
	 * @param modal the modal to set
	 */
	public void setModal(boolean modal) {
		this.modal = modal;
	}

	private Component getParent(Component comp) {
		Component prnt = comp;
		while(!((prnt instanceof Listitem) || (prnt instanceof Row))) {
			prnt = prnt.getParent();
		}
		return prnt;
	}

	public String getViewPage() {
	    return viewPage;
	}

	public void setViewPage(String viewPage) {
	    this.viewPage = viewPage;
	}

	public Span getBtnView() {
	    return btnView;
	}

	public void setBtnView(Span btnView) {
	    this.btnView = btnView;
	}

	public Span getBtnEdit() {
	    return btnEdit;
	}

	public void setBtnEdit(Span btnEdit) {
	    this.btnEdit = btnEdit;
	}

	public Span getBtnDelete() {
	    return btnDelete;
	}

	public void setBtnDelete(Span btnDelete) {
	    this.btnDelete = btnDelete;
	}
}
