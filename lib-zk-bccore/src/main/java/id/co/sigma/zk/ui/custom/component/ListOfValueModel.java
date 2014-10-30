package id.co.sigma.zk.ui.custom.component;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.AbstractListModel;

public class ListOfValueModel extends AbstractListModel<ListOfValueItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<ListOfValueItem> list = new ArrayList<ListOfValueItem>();
	
	public ListOfValueModel(List<ListOfValueItem> list) {
		super();
		this.list = list;
	}

	@Override
	public ListOfValueItem getElementAt(int index) {
		if(list == null & list.isEmpty()) return null;
		ListOfValueItem item = list.get(index);
		item.setServerObject(true);
		return item;
	}

	@Override
	public int getSize() {
		if(list == null & list.isEmpty()) return 0;
		return list.size();
	}

}
