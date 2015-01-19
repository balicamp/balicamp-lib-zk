package id.co.sigma.zk.ui.controller.base.window;

import id.co.sigma.zk.ui.annotations.ChildGridData;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zhtml.Td;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Radio;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

public class EditorWindow extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4855931629516556573L;
	
	private static final String MARK_REQUIRED_CSS = "sign-mandatory";
	
	private static final String MARK_REQUIRED = "*";
	
	/**
	 * jumlah child component window template 
	 */
	private int childrenCount = 5;
	
	@Wire
	private Panelchildren panelEditor;
	
	@Wire
	Label caption;
	
	@Wire
	private Timer lovLoaderTimer;
	
	
	@Wire
	protected Button btnSave ;
	
	@Wire
	protected Button btnCancel; 
	private String cancellationMsg;
	
	private String confirmationMsg;
	
	private BaseSimpleController controller;
	
	private List<Component> requiredFields;
	
	
	/**
	 * ini untuk show /hide cancel button. 
	 * set ini = false untuk hide cancel(batal) button 
	 */
	private boolean showCancelButton  = true;
	
	
	/**
	 * ini untuk show / hide untuk tombol save
	 */
	private boolean showSaveButton = true ; 
	
	private Component firstInputComp;
	
	public EditorWindow() {
		Executions.createComponents("~./zul/pages/common/EditorWindow.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireVariables(this, this, null);
		Selectors.wireEventListeners(this, this);
		childrenCount = this.getChildren().size(); 
	}

	public void setCaptionLabel(String caption) {
		this.caption.setValue(caption);
	}
	
	public String getCaptionLabel() {
		return this.caption.getValue();
	}
	
	@Override
	public void afterCompose() {
		
		requiredFields = new ArrayList<Component>();
		
		String title = getTitle();
		
		if(title != null && title.trim().length() > 0) {
			setCaptionLabel(title);
		}
		
		setBorder("none");
		setTitle("");
		
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
		
		try {
			controller = (BaseSimpleController)getAttribute(getId() + "$composer");
		} catch (Exception e) {}
		
		List<Component> children = getChildren();
		int dynaChildren = children.size() - childrenCount;
		for(int i = 0; i < dynaChildren; i++) {
			Component child = children.get(childrenCount);
			panelEditor.appendChild(child);
		}
		
		markRequiredFields();
		btnCancel.setVisible(showCancelButton); 
		btnSave.setVisible(showSaveButton);
		
		if(firstInputComp != null) {
			btnSave.setTabindex(1000);
			btnCancel.setTabindex(1001);
			focusFirstInput();
			EventListener<Event> onBlurListener = new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					focusFirstInput();					}
			};
			if(btnCancel.isVisible()) {
				btnCancel.addEventListener("onBlur", onBlurListener);
			} else if(!btnCancel.isVisible() && btnSave.isVisible()) {
				btnSave.addEventListener("onBlur", onBlurListener);
			}
		}
		
		composed = true ; 
	}

	@Listen("onTimer = #lovMasterTimer")
	public void forceStopLovTimer() {
		if(lovLoaderTimer != null && lovLoaderTimer.isRunning()) {
			lovLoaderTimer.stop();
			Clients.clearBusy(this);
		}
	}
	
	protected boolean composed = false ; 
	public String getCancellationMsg() {
		return cancellationMsg;
	}

	public void setCancellationMsg(String cancellationMsg) {
		setAttribute("cancellationMsg", cancellationMsg);
		this.cancellationMsg = cancellationMsg;
	}

	public String getConfirmationMsg() {
		return confirmationMsg;
	}

	public void setConfirmationMsg(String confirmationMsg) {
		setAttribute("confirmationMsg", confirmationMsg);
		this.confirmationMsg = confirmationMsg;
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
	
	private void markRequiredFields() {
		List<String> childGrids = new ArrayList<String>();
		if(controller != null) {			
			Field[] fields = controller.getClass().getDeclaredFields();
			for(Field f : fields) {
				if(f.isAnnotationPresent(ChildGridData.class)) {
					ChildGridData ann = f.getAnnotation(ChildGridData.class);
					childGrids.add(ann.gridId());
				}
			}
			
		}
		List<Component> comps = new ArrayList<Component>(getFellows());
		for(Component comp : comps) {
			controller.orderInputField(comp);
			getFirstInputComponent(comp);
			if(comp instanceof InputElement) {
				Constraint cons = ((InputElement)comp).getConstraint();
				if(((((InputElement)comp).getHflex() == null || "".equals(((InputElement)comp).getHflex())) 
						&& ((((InputElement) comp).getWidth() == null) || ("".equals(((InputElement) comp).getWidth())))) 
						&& !((comp instanceof Combobox) || (comp instanceof Datebox) || (comp instanceof Bandbox))) {
					((InputElement)comp).setHflex("1");
				}
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
	
	/**
	 * ini untuk show /hide cancel button. 
	 * set ini = false untuk hide cancel(batal) button 
	 */
	public boolean isShowCancelButton() {
		return showCancelButton;
	}
	/**
	 * ini untuk show /hide cancel button. 
	 * set ini = false untuk hide cancel(batal) button 
	 */
	public void setShowCancelButton(boolean showCancelButton) {
		this.showCancelButton = showCancelButton;
		if ( composed){
			btnCancel.setVisible(this.showCancelButton); 
		}
	}
	/**
	 * ini untuk show / hide untuk tombol save
	 */
	public void setShowSaveButton(boolean showSaveButton) {
		this.showSaveButton = showSaveButton;
		if ( composed){
			btnSave.setVisible(this.showSaveButton); 
		}
	}
	/**
	 * ini untuk show / hide untuk tombol save
	 */
	public boolean isShowSaveButton() {
		return showSaveButton;
	}
}
