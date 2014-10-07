package id.co.sigma.zk.ui.custom.component;

import java.io.Serializable;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;

/**
 * List window template layout
 * @author windu
 *
 */
public class ListWindow extends Window implements AfterCompose, IdSpace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	@Wire
	private Label caption;
	
	@Wire
	private Panelchildren searchSection;
	
	@Wire
	private Panelchildren listSection;
	
	private int childrenCount = 6;
	
	private BaseSimpleListController<Serializable> listController;
	
	private String editorPage;

	public ListWindow() {
		Executions.createComponents("~./zul/pages/common/ListWindow.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);
		Selectors.wireVariables(this, this, null);
		childrenCount = getChildren().size();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterCompose() {
		setBorder("none");
		String title = getTitle();
		caption.setValue(title);
		setTitle("");
		
		listController = (BaseSimpleListController<Serializable>) getAttribute(getId() + "$composer");
		
		searchSection.appendChild(getChildren().get(childrenCount));
		listSection.appendChild(getChildren().get(childrenCount));
		
	}
	
	@Listen("onClick = #btnSearch")
	public void onClickButtonSearch() {
		if(listController != null) {
			listController.searchData();
		}
	}

	@Listen("onClick = #btnReset")
	public void onClickButtonReset() {
		if(listController != null) {
			listController.resetSearchFilter();
		}
	}
	
	@Listen("onClick = #btnAddNew")
	public void onClickButtonAddNew() {
		if(listController != null) {
			EditorManager.getInstance().addNewData(editorPage, (SingleKeyEntityData<?>)listController.addNewData(), listController);
		}
	}

	/**
	 * @return the editorPage
	 */
	public String getEditorPage() {
		return editorPage;
	}

	/**
	 * @param editorPage the editorPage to set
	 */
	public void setEditorPage(String editorPage) {
		this.editorPage = editorPage;
	}

	
}
