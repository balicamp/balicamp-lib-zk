package id.co.sigma.zk.ui.custom.component;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
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
		
		String sEvent = "onChange";
		if(com instanceof InputElement) {
			sEvent = "onChange";
		} else if(com instanceof Checkbox) {
			sEvent = "onCheck";
		}
		
		if(isGrid) {
			/*com.setWidgetListener("onChange", "var wDom = jq('#' + '"+uuid+"'); "
					+ "var lO = zk.Widget.$('$"+uuid+"');"
					+ "var _s=lO.getValue();"
					+ "var _l='+';"
					+ "if((_s=='') || (_s == '*')){_l='*';}"
					+ "lO.setValue(_l);"
					+ "lO.smartUpdate('value',_l);");*/
			
			com.setWidgetListener(sEvent, "var wDom = jq('#' + '"+uuid+"'); "
					+ "var lO = zk.Widget.$('$"+uuid+"');"
					+ "var _s=lO.getSclass();"
					+ "var _l='z-icon-asterisk';"
					+ "console.log(_s);"
					+ "if((_s==undefined) || (_s=='') || (_s == 'z-icon-pencil')){_l='z-icon-pencil';}"
					+ "lO.setSclass(_l);" 
					+ "lO.smartUpdate('sclass', _l);");
		} else {
			/*com.setWidgetListener("onChange", "var wDom = jq('#' + '"+uuid+"'); "
					+ "var lO = zk.Widget.$('$"+uuid+"');"
					+ "var _s=lO.getLabel();"
					+ "var _l='+';"
					+ "if((_s=='') || (_s == '*')){_l='*';}"
					+ "lO.setLabel(_l);"
					+ "lO.smartUpdate('label',_l);");*/
			
			com.setWidgetListener(sEvent, "var wDom = jq('#' + '"+uuid+"'); "
					+ "var lO = zk.Widget.$('$"+uuid+"');"
					+ "var _s=lO.getIconSclass();"
					+ "var _l='z-icon-asterisk';"
					+ "if((_s==undefined) || (_s=='') || (_s == 'z-icon-pencil')){_l='z-icon-pencil';}"
					+ "lO.setIconSclass(_l);"
					+ "lO.smartUpdate('iconSclass',_l);");
		}
//		}
	}
}
