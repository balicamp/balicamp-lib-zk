package id.co.sigma.zk.ui.controller.dualcontrol;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;

public class DualControlWindow extends Window implements AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6015970316438404062L;
	
	@Wire
	private Label caption; 
	
	@Wire
	private Panelchildren windowContent;
	
	public DualControlWindow() {
		super();
		Map<?, ?> args = Executions.getCurrent().getArg();
		System.out.println(args.get("userApproval"));

		
		Executions.createComponents("~./zul/pages/dualctrl/DualControlWindow.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);
		
		setPageContentSrc((String)args.get("userApproval"));
	}

	public void setPageContentSrc(String pageSrc) {
		content = new WindowContent(pageSrc);
		content.setParent(windowContent);
	}

	public String getPageContentSrc() {
		if(content != null)  return content.getPageSrc();
		return "";
	}

	WindowContent content;
	
	public class WindowContent extends Groupbox implements IdSpace {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7664126486211414678L;
		
		private String pageSrc;

		public String getPageSrc() {
			return pageSrc;
		}

		public WindowContent(String pageSrc) {
			this.pageSrc = pageSrc;
			Executions.createComponents(pageSrc, this, null);
			Selectors.wireComponents(this, this, false);
			Selectors.wireEventListeners(this, this);
		}
		
		
	}

	@Override
	public void afterCompose() {
		Map<?, ?> args = Executions.getCurrent().getArg();
		System.out.println(args);
	}

}
