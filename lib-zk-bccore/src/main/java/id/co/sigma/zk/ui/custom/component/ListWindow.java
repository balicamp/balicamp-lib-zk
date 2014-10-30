package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Timer;
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
//	private Panelchildren listSection;
//	private Vbox listSection;
	private Div listSection;
	
	@Wire
	private Panel panelSearchKeys;
	
	@Wire
	private Panel panelButtons;
	
	@Wire
	private Button btnAddNew;

	@Wire
	private Button btnSearch;
	
	@Wire
	private Button btnReset;
	
	@Wire
	private Timer listTimer;

	@Wire
	private Timer lovLoaderTimer;
	
	private int childrenCount = 6;
	
	private BaseSimpleListController<Serializable> listController;
	
	private String editorPage;
	
	private boolean searchable = true; 
	
	private boolean addable = true;

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
		setStyle("overflow:auto");
		setHeight(""); //reset height
		setVflex(""); //rest vflex
		
		addEventListener("onLoadCombodata", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(lovLoaderTimer != null) {
					lovLoaderTimer.start();
					Clients.showBusy(event.getTarget(), "Load combo data...");
				}
			}
		});

		addEventListener("onStopTimer", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(lovLoaderTimer != null) {
					lovLoaderTimer.stop();
					Clients.clearBusy(event.getTarget());
				}
			}
		});
		
		listController = (BaseSimpleListController<Serializable>) getAttribute(getId() + "$composer");
		
		panelSearchKeys.setVisible(isSearchable());
		btnSearch.setVisible(isSearchable());
		btnReset.setVisible(isSearchable());
		if(!isSearchable() && !isAddable()) {
			panelButtons.setVisible(false);
		}

		if((getChildren().size() - childrenCount) >= 2) {
			
			Component srchSection = getChildren().get(childrenCount);
			srchSection.setVisible(isSearchable());
			searchSection.appendChild(srchSection);
			
		}
		
		Component child = null;
		
		for(;getChildren().size() > childrenCount;) {
			
			child = getChildren().get(childrenCount);
			
			if(child instanceof Listbox) break;
			
			searchSection.appendChild(child);
		}
		
		if(child != null) {
			if(child instanceof Listbox) {
				((Listbox)child).setHeight(""); //reset heigh
				((Listbox)child).setVflex("1");
			}
			listSection.appendChild(child);
		}
		
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
	
	@Listen("onTimer = #listTimer")
	public void fecthData() {
		if((listController != null) && (listController instanceof IReloadablePanel)) {
			((IReloadablePanel)listController).reload();
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

	/**
	 * @return the searchable
	 */
	public boolean isSearchable() {
		return searchable;
	}

	/**
	 * @param searchable the searchable to set
	 */
	public void setSearchable(String searchable) {
		this.searchable = Boolean.valueOf(searchable);
	}

	/**
	 * @return the addable
	 */
	public boolean isAddable() {
		return addable;
	}

	/**
	 * @param addable the addable to set
	 */
	public void setAddable(String addable) {
		this.addable = Boolean.valueOf(addable);
	}

	
}
