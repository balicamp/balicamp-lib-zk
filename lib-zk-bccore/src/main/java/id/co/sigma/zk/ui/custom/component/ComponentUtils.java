package id.co.sigma.zk.ui.custom.component;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

public final class ComponentUtils {
	
	/**
	 * get parent window
	 * @return
	 */
	public static final Window getParentWindow(Component child) {
		Component parent = child.getParent();
		if(parent instanceof Window) {
			return (Window)parent;
		}
		for(;!(parent instanceof Window);) {
			parent = parent.getParent();
			if(parent instanceof Window) {
				return (Window)parent;
			}
		}
		return null;
		
	}
	
	/**
	 * get window controller
	 * @param child
	 * @return
	 */
	public static final Object getWindowController(Component child) {
		Window win = getParentWindow(child);
		if(win != null) {
			return win.getAttribute(win.getId() + "$composer");
		}
		return null;
	}
}
