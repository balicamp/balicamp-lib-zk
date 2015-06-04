package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.data.SingleKeyEntityData;
import id.co.sigma.zk.ui.annotations.ChildGridData;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zhtml.Td;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Radio;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

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
	
	private static final String MARK_REQUIRED_CSS = "sign-mandatory";
	
	private static final String MARK_REQUIRED = "*";
	
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
	private Panel panelTitle;
	
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
	
	private boolean modalEditor = false;
	
	private boolean showPanelTitle = true;
	
	private boolean showSearchButton = true;
	
	private boolean showResetButton = true;
	
	private boolean showAddButton = true;
	
	private List<Component> requiredFields;
	
	private Component firstInputComp;

	public ListWindow() {
		Executions.createComponents("~./zul/pages/common/ListWindow.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);
		Selectors.wireVariables(this, this, null);
		childrenCount = getChildren().size();
	}
	
	public void setCaption(String value){
		caption.setValue(value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterCompose() {
		setBorder("none");
		String title = getTitle();
		setCaption(title);
		setTitle("");
		setStyle("overflow:auto");
		setHeight(""); //reset height
		setVflex(""); //rest vflex
		
		requiredFields = new ArrayList<Component>();
		
		addEventListener("onLoadCombodata", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(lovLoaderTimer != null && !lovLoaderTimer.isRunning()) {
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
		
		panelTitle.setVisible(isShowPanelTitle());
		panelSearchKeys.setVisible(isSearchable());
		btnSearch.setVisible(isSearchable() && isShowSearchButton());
		btnReset.setVisible(isSearchable() && isShowResetButton());
		btnAddNew.setVisible(isAddable() && isShowAddButton());
		if(!isSearchable() && !isAddable()) {
			panelButtons.setVisible(false);
		}else if(!isShowSearchButton() && !isShowResetButton() && !isShowAddButton()){
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
		
		markRequiredFields();
		
		if(firstInputComp != null) {
			
			btnSearch.setTabindex(1000);
			btnReset.setTabindex(1001);
			btnAddNew.setTabindex(1002);
			
			focusFirstInput();
			EventListener<Event> onBlurListener = new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					focusFirstInput();					}
			};
			
			if(btnAddNew.isVisible()) {
				btnAddNew.addEventListener("onBlur", onBlurListener);
			} else if(!btnAddNew.isVisible() && btnReset.isVisible()) {
				btnReset.addEventListener("onBlur", onBlurListener);
			} else if(!btnAddNew.isVisible() && !btnReset.isVisible() && btnSearch.isVisible()) {
				btnSearch.addEventListener("onBlur", onBlurListener);
			}
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
			EditorManager.getInstance().addNewData(editorPage, (SingleKeyEntityData<?>)listController.addNewData(), listController, modalEditor);
		}
	}
	
	@Listen("onTimer = #listTimer")
	public void fecthData() {
		if((listController != null) && (listController instanceof IReloadablePanel)) {
			((IReloadablePanel)listController).reload();
		}
	}
	
	@Listen("onTimer = #lovMasterTimer")
	public void forceStopLovTimer() {
		if(lovLoaderTimer != null && lovLoaderTimer.isRunning()) {
			lovLoaderTimer.stop();
			Clients.clearBusy(this);
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

	/**
	 * @return the modalEditor
	 */
	public boolean isModalEditor() {
		return modalEditor;
	}

	public boolean isShowPanelTitle() {
		return showPanelTitle;
	}

	public void setShowPanelTitle(boolean showPanelTitle) {
		this.showPanelTitle = showPanelTitle;
	}

	/**
	 * @param modalEditor the modalEditor to set
	 */
	public void setModalEditor(String modalEditor) {
		this.modalEditor = Boolean.valueOf(modalEditor);
	}

	public void clearErrorMessage() {
		if(requiredFields == null) return;
		for(Component cr : requiredFields) {
			((InputElement)cr).clearErrorMessage();
		}
	}
	
	public void addRequiredField(Component cr) {
		if(requiredFields==null) {
			requiredFields = new ArrayList<Component>();
		}
		if((cr instanceof InputElement) && ((InputElement)cr).getConstraint() != null) {
			if(!requiredFields.contains(cr)) {
				int tabIdx = ((InputElement)cr).getTabindex();
				if(tabIdx >= requiredFields.size()) {
					requiredFields.add(cr);
				} else if(tabIdx >= 0) {
					requiredFields.add(tabIdx, cr);
				}
			}
		}
	}
	
	private void focusFirstInput() {
		if(firstInputComp instanceof InputElement) {
			((InputElement)firstInputComp).setFocus(true);
		} else if(firstInputComp instanceof Checkbox) {
			((Checkbox)firstInputComp).setFocus(true);
		} else if(firstInputComp instanceof Radio) {
			((Radio)firstInputComp).setFocus(true);
		}
	}
	
	private void getFirstInputComponent(Component input) {
		int tabIdx = Integer.MAX_VALUE;
		if(input instanceof InputElement) {
			tabIdx = ((InputElement)input).getTabindex();
		} else if(input instanceof Checkbox) {
			tabIdx = ((Checkbox)input).getTabindex();
		} else if(input instanceof Radio) {
			tabIdx = ((Radio)input).getTabindex();
		}
		if(tabIdx == 1) {
			firstInputComp = input;
		}
	}
	
	private void markRequiredFields() {
		List<String> childGrids = new ArrayList<String>();
		if(listController != null) {			
			Field[] fields = listController.getClass().getDeclaredFields();
			for(Field f : fields) {
				if(f.isAnnotationPresent(ChildGridData.class)) {
					ChildGridData ann = f.getAnnotation(ChildGridData.class);
					childGrids.add(ann.gridId());
				}
			}
			
		}
		List<Component> comps = new ArrayList<Component>(getFellows());
		
		EventListener<Event> okEvent = new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(!(event.getTarget() instanceof Button)) {
					Events.sendEvent("onClick", btnSearch, null);
				}
			}
		};
		
		for(Component comp : comps) {
			listController.orderInputField(comp);
			getFirstInputComponent(comp);
			if(comp instanceof InputElement) {
				Constraint cons = ((InputElement)comp).getConstraint();

				comp.addEventListener("onOK", okEvent);
				
				if(cons != null && (((InputElement)comp).isReadonly() || ((InputElement)comp).isDisabled())) {
					((InputElement)comp).addEventListener("onBlur", new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Component thisComp = event.getTarget();
							if(thisComp instanceof InputElement) {
								((InputElement)thisComp).clearErrorMessage();
							}
						}
					});
				}
				if(cons instanceof SimpleConstraint) {
					SimpleConstraint sc = (SimpleConstraint) cons;
					int reqFlag = sc.getFlags() & SimpleConstraint.NO_EMPTY;
					if(reqFlag == SimpleConstraint.NO_EMPTY) {
						requiredFields.add(comp);
						Component prev = comp.getPreviousSibling();
						if((prev != null) && (prev instanceof Label)) {
							comp.getParent().insertBefore(createMarkRequired(prev), comp);
						} else { //jika dalam Cell ambil parent-nya
							Component prn = comp.getParent();
							Component ps = prn.getPreviousSibling();
							if((ps != null) && (ps instanceof Label)) {
								prn.getParent().insertBefore(createMarkRequired(ps), prn);
							} else if((ps != null) && ((ps instanceof Cell) || (ps instanceof Td))) {
								List<Component> chs = ps.getChildren();
								boolean marked = false;
								for(Component c : chs) {
									if(c instanceof HtmlBasedComponent) {
										String sclass = ((HtmlBasedComponent)c).getSclass();
										if(sclass != null && sclass.contains(MARK_REQUIRED_CSS)) {
											marked = true;
											break;
										}
									}
								}
								if(!marked) {
									Label mreq = new Label(MARK_REQUIRED);
									mreq.setClass(MARK_REQUIRED_CSS);
									ps.appendChild(mreq);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private Cell createMarkRequired(Component prev) {
		Label mreq = new Label(MARK_REQUIRED);
		mreq.setClass(MARK_REQUIRED_CSS);
		Cell cell = new Cell();
		cell.setClass("z-row-inner");
		cell.appendChild(prev);
		cell.appendChild(mreq);
		return cell;
	}

	public boolean isShowSearchButton() {
		return showSearchButton;
	}

	public void setShowSearchButton(boolean showSearchButton) {
		this.showSearchButton = showSearchButton;
	}

	public boolean isShowResetButton() {
		return showResetButton;
	}

	public void setShowResetButton(boolean showResetButton) {
		this.showResetButton = showResetButton;
	}

	public boolean isShowAddButton() {
		return showAddButton;
	}

	public void setShowAddButton(boolean showAddButton) {
		this.showAddButton = showAddButton;
	}
	
}
