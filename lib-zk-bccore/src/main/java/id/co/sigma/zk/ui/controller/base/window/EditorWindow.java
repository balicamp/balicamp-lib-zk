package id.co.sigma.zk.ui.controller.base.window;

import id.co.sigma.zk.ui.annotations.ChildGridData;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panelchildren;
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
	
	private String cancellationMsg;
	
	private String confirmationMsg;
	
	private BaseSimpleController controller;
	
	private List<Component> requiredFields;
	
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
	}

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
			if(comp instanceof InputElement) {
				Constraint cons = ((InputElement)comp).getConstraint();
				if((((InputElement)comp).getHflex() == null || "".equals(((InputElement)comp).getHflex())) 
						&& !((comp instanceof Combobox) || (comp instanceof Datebox) || (comp instanceof Bandbox))) {
					((InputElement)comp).setHflex("1");
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
							} else if((ps != null) && (ps instanceof Cell)) {
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
}
