/**
 * 
 */
package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.base.BaseSimpleTreeController;

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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treeitem;


/**
 * Action Button untuk tree > ada tiga button, add, edit, delete
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public class ActionButtonTree extends Div implements IdSpace, AfterCompose {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1000189904274471089L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(ActionButtonTree.class.getName());
	
	public static final Integer ADD_BUTTON = 0;
	public static final Integer EDIT_BUTTON = 1;
	public static final Integer DELETE_BUTTON = 2;
	
	private BaseSimpleTreeController<Serializable> controller;
	
	private Class<? extends SingleKeyEntityData<Long>>  entityClass;
	
	private String editEvent;
	private String deleteEvent;
	private String addEvent;
	
	private String targetPath;
	private String editorPage;
	
	private String deleteMsg;
	
	public ActionButtonTree() {
		Executions.createComponents("~./zul/pages/common/ActionButtonTree.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);		
	}

	public BaseSimpleTreeController<Serializable> getController() {
		return controller;
	}

	public void setController(BaseSimpleTreeController<Serializable> controller) {
		this.controller = controller;
	}

	public Class<? extends SingleKeyEntityData<Long>> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(
			Class<? extends SingleKeyEntityData<Long>> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	public void afterCompose() {
		List<Component> children = getChildren();
		Component addComp = children.get(ADD_BUTTON);
		Component editComp = children.get(EDIT_BUTTON);
		Component delComp = children.get(DELETE_BUTTON);
		
		addComp.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				
				Component comp = event.getTarget().getParent().getParent().getParent().getParent().getParent();
				Treeitem item = (Treeitem)comp;
				TreeNode<SingleKeyEntityData<Long>> treeNode = item.getValue();
				
				EditorManager.getInstance().addNewData(editorPage,entityClass.newInstance(),treeNode.getData(), controller);
				
			}
		});
		
		editComp.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				
				Component comp = event.getTarget().getParent().getParent().getParent().getParent().getParent();
				Treeitem item = (Treeitem)comp;
				TreeNode<SingleKeyEntityData<Long>> treeNode = item.getValue();
				
				EditorManager.getInstance().editData(editorPage,treeNode.getData(), controller);
			}
			
		});
		
		delComp.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				
				Component comp = event.getTarget().getParent().getParent().getParent().getParent().getParent();
				Treeitem item = (Treeitem)comp;
				final TreeNode<SingleKeyEntityData<Long>> treeNode = item.getValue();
				
				if(deleteMsg == null || deleteMsg.trim().length() == 0) {
					deleteMsg = "Apakah anda yakin akan menghapus data ?";
				}
				
				Messagebox.show(deleteMsg, "Delete Confirmation", Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						switch(((Integer)event.getData()).intValue()) {
						case Messagebox.YES:
							controller.deleteData(treeNode.getData());
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

	public String getAddEvent() {
		return addEvent;
	}

	public void setAddEvent(String addEvent) {
		this.addEvent = addEvent;
	}
	
	
	
}
