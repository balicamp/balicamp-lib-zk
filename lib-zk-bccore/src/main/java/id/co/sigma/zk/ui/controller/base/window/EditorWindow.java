package id.co.sigma.zk.ui.controller.base.window;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;

public class EditorWindow extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4855931629516556573L;
	
	/**
	 * jumlah child component window template 
	 */
	private int childrenCount = 5;
	
	@Wire
	private Panelchildren panelEditor;
	
	@Wire
	Label caption;
	
	private String cancellationMsg;
	
	private String confirmationMsg;
	
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
		
		String title = getTitle();
		
		if(title != null && title.trim().length() > 0) {
			setCaptionLabel(title);
		}
		
		setBorder("none");
		setTitle("");
		
		List<Component> children = getChildren();
		int dynaChildren = children.size() - childrenCount;
		for(int i = 0; i < dynaChildren; i++) {
			Component child = children.get(childrenCount);
			panelEditor.appendChild(child);
		}

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

	
}
