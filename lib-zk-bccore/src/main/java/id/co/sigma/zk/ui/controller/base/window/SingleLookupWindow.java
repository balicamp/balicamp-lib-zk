package id.co.sigma.zk.ui.controller.base.window;

import java.util.List;

import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;

/**
 *
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class SingleLookupWindow extends Window implements AfterCompose {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6985796241554625329L;
	private static final Logger logger = LoggerFactory.getLogger(SingleLookupWindow.class.getName()); 
	@Wire
	private Panelchildren panelLookup;
	
	
	BaseSimpleController controller;
	
	/**
	 * jumlah child component window template 
	 */
	private int childrenCount = 4;
	
	public SingleLookupWindow() {
		Executions.createComponents("~./zul/pages/common/SingleLookupWindow.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireVariables(this, this, null);
		Selectors.wireEventListeners(this, this);
		childrenCount = this.getChildren().size(); 
	}

	@Override
	public void afterCompose() {
		try {
			controller = (BaseSimpleController)getAttribute(getId() + "$composer");
		} catch (Exception e) {
			logger.error("gagal membaca controller class : " + getClass().getName() +", error : " + e.getMessage()   , e);
		}
		List<Component> children = getChildren();
		int dynaChildren = children.size() - childrenCount;
		for(int i = 0; i < dynaChildren; i++) {
			Component child = children.get(childrenCount);
			panelLookup.appendChild(child);
		}

		
		
		
	}
	
	
	
}
