package id.co.sigma.zk.ui.custom.component;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Listhead;

public class ActionListhead extends Listhead implements IdSpace, AfterCompose {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ActionListhead() {
		Executions.createComponents("~./zul/pages/common/ActionListhead.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireVariables(this, this, null);
		Selectors.wireEventListeners(this, this);
	}

	@Override
	public void afterCompose() {
		List<Component> children = getChildren();
		Component[] dynamics = new Component[children.size()-2];
		Component[] defaults = new Component[2];
		Component[] alls = children.toArray(new Component[children.size()]);
		System.arraycopy(alls, 0, defaults, 0, 2);
		System.arraycopy(alls, 2, dynamics, 0, children.size()-2);
		children.clear();
		for(Component cmp : dynamics) {
			children.add(cmp);
		}
		for(Component cmp : defaults) {
			children.add(cmp);
		}
	}

}
