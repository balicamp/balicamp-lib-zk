package id.co.sigma.zk.ui.custom.component;

import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

public class ListOfValueComboitemRenderer implements ComboitemRenderer<ListOfValueItem>{

	private String defaultValue;
	private boolean found = false;
	
	public ListOfValueComboitemRenderer(String defaultValue) {
		super();
		this.defaultValue = defaultValue;
		this.found = false;
	}

	@Override
	public void render(Comboitem item, ListOfValueItem data, int index)
			throws Exception {
		item.setLabel(data.toString());
		item.setValue(data.getValue());
		if(data.getValue() != null) {
			if((item.getParent() instanceof Combobox) && (data.getValue().toString().equals(defaultValue))) {
				((Combobox)item.getParent()).setValue(item.getLabel());
				this.found = true;
			} else if(item.getParent() instanceof Combobox) {
				try {
					if(!this.found) ((Combobox)item.getParent()).setValue("");
				} catch (Exception e) {
					((Combobox)item.getParent()).clearErrorMessage();
				}
			}
		}
	}

}
