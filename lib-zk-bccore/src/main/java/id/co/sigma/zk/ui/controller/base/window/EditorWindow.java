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
	}

	public void setCaptionLabel(String caption) {
		this.caption.setValue(caption);
	}
	
	public String getCaptionLabel() {
		return this.caption.getValue();
	}
	
	@Override
	public void afterCompose() {
		List<Component> children = getChildren();
		if(children.size() > 0) {
			Component child = children.get(children.size()-1);
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
