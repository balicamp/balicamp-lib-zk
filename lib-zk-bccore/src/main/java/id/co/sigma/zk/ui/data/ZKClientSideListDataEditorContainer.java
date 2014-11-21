package id.co.sigma.zk.ui.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.ext.Sortable;

import id.co.sigma.common.data.ClientSideListDataEditorContainer;

/**
 * data container in memory untuk ZK
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public class ZKClientSideListDataEditorContainer<DATA> extends ClientSideListDataEditorContainer<DATA>  implements ListModel<DATA>  , Selectable<DATA>, Sortable<DATA>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4561414899101058858L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(ZKClientSideListDataEditorContainer.class.getName());
	
	
	
	private boolean multiple =false  ; 
	
	
	private HashSet<ListDataListener> listeners = new HashSet<ListDataListener>() ; 
	
	private Comparator<DATA> sorting;
	
	private boolean sortDir;
	
	
	private HashSet<DATA> selectedItems = new HashSet<DATA>(); 
	@Override
	public boolean addToSelection(DATA data) {
		if ( selectedItems.contains(data))
			return false ; 
		selectedItems.add(data);
		return true;
	}
	@Override
	public void clearSelection() {
		selectedItems.clear(); 
		
	}
	@Override
	public Set<DATA> getSelection() {
		return selectedItems;
	}
	@Override
	public boolean isMultiple() {
		return multiple;
	}
	@Override
	public boolean isSelected(Object data) {
		return selectedItems.contains(data);
	}
	@Override
	public boolean isSelectionEmpty() {
		return selectedItems.isEmpty();
	}
	@Override
	public boolean removeFromSelection(Object data) {
		
		return selectedItems.remove(data); 
	}
	@Override
	public void setMultiple(boolean multiple) {
		this.multiple = multiple ; 
		
	}
	@Override
	public void setSelection(Collection<? extends DATA> selection) {
		
		selectedItems.clear();
		if ( selection== null || selection.isEmpty())
			return ; 
		selectedItems.addAll(selection); 		
	}
	@Override
	public void addListDataListener(ListDataListener listener) {
		listeners.add(listener); 
		
	}
	@Override
	public DATA getElementAt(int idx) {
		if ( idx<0 || idx>= allStillExistData.size())
			return null ; 
		return allStillExistData.get(idx);
	}
	@Override
	public int getSize() {
		if ( allStillExistData == null)
			return 0 ; 
		return allStillExistData.size();
	}
	@Override
	public void removeListDataListener(ListDataListener listener) {
		listeners.remove(listener);
		
	}
	
	@Override
	public void appendNewItem(DATA data) {
		int idxLast = allStillExistData.size();
		super.appendNewItem(data);
		fireEvent(ListDataEvent.INTERVAL_ADDED, idxLast, idxLast);
	}
	
	@Override
	public void eraseData(DATA data) {
		int i = 0 ; 
		for ( DATA scn : allStillExistData) {
			if ( data.equals(scn))
				break;
			i++ ; 
		}
		super.eraseData(data);
		fireEvent(ListDataEvent.INTERVAL_REMOVED, i, i);
	}
	
	
	
	
	@Override
	public void modifyItem(DATA data, boolean fireChangeEvent) {
		if ( !fireChangeEvent){
			super.modifyItem(data, false);
			return ; 
		}
		
		int i = 0 ; 
		for ( DATA scn : allStillExistData) {
			if ( data.equals(scn))
				break;
			i++ ; 
		}
		super.modifyItem(data, fireChangeEvent);
		fireEvent(ListDataEvent.CONTENTS_CHANGED, i, i);
	}
	
	
	
	protected void fireEvent (int eventType , int startIndex , int lastIndex ) {
		if ( this.listeners.isEmpty())
			return ; 
		ListDataEvent evt = new ListDataEvent(this , eventType , startIndex , lastIndex); 
		for ( ListDataListener scn : listeners ) {
			scn.onChange(evt);
		}
	}
	
	@Override
	public void sort(Comparator<DATA> cmpr, boolean ascending) {
		sorting = cmpr;
		sortDir = ascending;
		Collections.sort(allStillExistData, cmpr);
		fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
	}
	
	@Override
	public String getSortDirection(Comparator<DATA> cmpr) {
		if (Objects.equals(sorting, cmpr))
			return sortDir ?
					"ascending" : "descending";
		return "natural";	
	}
}
