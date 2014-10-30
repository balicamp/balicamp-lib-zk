package id.co.sigma.zk.ui.custom.component;

import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

public class ListOfValueComboitemRenderer implements ComboitemRenderer<ListOfValueItem>{

	@Override
	public void render(Comboitem item, ListOfValueItem data, int index)
			throws Exception {
		item.setLabel(data.toString());
		item.setValue(data.getValue());
	}

}
