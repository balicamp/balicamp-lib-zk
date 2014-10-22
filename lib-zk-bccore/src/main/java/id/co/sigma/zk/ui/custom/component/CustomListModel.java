/**
 * 
 */
package id.co.sigma.zk.ui.custom.component;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListSubModel;

/**
 * class ini untuk mengakomodir proses autocomplete pada combobox, #ListModelList tidak bisa mengakomodir ini
 * sehingga perlu dicustom
 * 
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public class CustomListModel<E> extends ListModelList<E> implements
		ListSubModel<E> {
	
	public CustomListModel(List<E> data){
		this._list=data;
	}
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2370704615334883750L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(CustomListModel.class.getName());
	
	@Override
	public ListModel<E> getSubModel(Object value, int nRows) {
		final List<E> data = new LinkedList<E>();
		nRows = getMaxNumberInSubModel(nRows);
		for (int i = 0; i < _list.size(); i++) {
			if (inSubModel(value, _list.get(i))) {
				data.add((E)_list.get(i));
				if (--nRows <= 0) break; //done
			}
		}
		return new ListModelList<E>(data);
	}
	
	protected int getMaxNumberInSubModel(int nRows) {
		return nRows < 0 ? 15: nRows;
	}
	
	protected boolean inSubModel(Object key, Object value) {
		String idx = objectToString(key);
		return idx.length() > 0 && objectToString(value).toLowerCase().startsWith(idx.toLowerCase());
	}
	
	protected String objectToString(Object value) {
		final String s = value != null ? value.toString(): "";
		return s != null ? s: "";
	}

	
}
