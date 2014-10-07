package id.co.sigma.zk.ui.custom.component;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.impl.InputElement;

public final class ActionUtils {
	public static final void registerClientEventListner(List<Component> coms, String uuid) {
		if(coms != null && !coms.isEmpty()) {
			Component com = coms.get(0);
			registerClientEventListner(com, uuid, false);
		}
	}

	public static final void registerClientEventListner(Component com, String uuid) {
		registerClientEventListner(com, uuid, true);
	}
	
	public static final void registerClientEventListner(Component com, String uuid, boolean isGrid) {
		if(com instanceof InputElement) {
			if(isGrid) {
				com.setWidgetListener("onChange", "var wDom = jq('#' + '"+uuid+"'); "
						+ "var lO = zk.Widget.$('$"+uuid+"');"
						+ "var _s=lO.getValue();"
						+ "var _l='+';"
						+ "if((_s=='') || (_s == '*')){_l='*';}"
						+ "lO.setValue(_l);"
						+ "lO.smartUpdate('value',_l);");
			} else {
				com.setWidgetListener("onChange", "var wDom = jq('#' + '"+uuid+"'); "
						+ "var lO = zk.Widget.$('$"+uuid+"');"
						+ "var _s=lO.getLabel();"
						+ "var _l='+';"
						+ "if((_s=='') || (_s == '*')){_l='*';}"
						+ "lO.setLabel(_l);"
						+ "lO.smartUpdate('label',_l);");
			}
		}
	}
}
