package id.co.sigma.zk.ui.custom.component;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;


public class SpringSecurityLogin extends Div implements IdSpace, AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Wire
	private HtmlNativeComponent f;
	
	private String action;

	public SpringSecurityLogin() {
		Executions.createComponents("~./zul/pages/security/SpringSecurityLogin.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);
	}
	
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return this.action;
	}


	@Override
	public void afterCompose() {
		
		Component c = this.getChildren().get(0);
		
		if(c instanceof Div) {
			
			HtmlNativeComponent form = (HtmlNativeComponent)c.getChildren().get(0);
			form.setDynamicProperty("action", this.action);
			
		} else if (c instanceof HtmlNativeComponent) {
			
			((HtmlNativeComponent)c).setDynamicProperty("action", this.action);
			
		}
		
	}

}
