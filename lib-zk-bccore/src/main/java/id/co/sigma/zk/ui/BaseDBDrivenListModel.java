package id.co.sigma.zk.ui;

import id.co.sigma.common.data.PagedResultHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;

/**
 * base class untuk list grid untuk data yang di drive dari database
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseDBDrivenListModel<DATA>  extends PagedResultHolder<DATA> implements ListModel<DATA>  ,   Selectable<DATA> {

	
	
	
	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 625093278502935790L;


	
	private int latestRowIndex  ; 
	
	
	private Set<DATA> selectedItems = new HashSet<DATA>(); 
	
	
	private boolean multiple; 
	@Override
	public void addListDataListener(ListDataListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DATA getElementAt(int index) {
		
		Integer swapFirst = getFirstRowPosition(); 
		int fst = swapFirst == null ? 0 : swapFirst; 
		if ( fst <= index &&  index<= latestRowIndex){
			return getAtWorker(index); 
		}
		int pageBaru = (int) Math.floor(((double)index) / (double)getPageSize());
		
		navigate(pageBaru);
		
		return getAtWorker(index);
	}
	
	
	
	private DATA getAtWorker (int index) {
		Integer swapFirst = getFirstRowPosition(); 
		int fst = swapFirst == null ? 0 : swapFirst; 
		List<DATA> ds =  getHoldedData();
		if ( ds== null)
			return null ;
		int idx = index - fst ;
		if ( idx< 0 || idx>= ds.size())
			return null ;
		//System.out.println("index : " + index + " ,   first : " + fst + ",index di ambil : "  + idx);
		return ds.get(idx); 
	}

	@Override
	public int getSize() {
		Integer swap =  getTotalData();
		return swap == null ? 0 : swap; 
	}

	@Override
	public void removeListDataListener(ListDataListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	@Override
	public void setHoldedData(List<DATA> holdedData) {
		super.setHoldedData(holdedData);
		if ( holdedData!= null && !holdedData.isEmpty()){
			latestRowIndex = getFirstRowPosition() +  holdedData.size() -1 ;
		}
	}
	
	/**
	 * initiate . ini di panggil pada saat di tekan tombol search . di awal. 
	 */
	public void initiate (int pageSize ) {
		 setPageSize(pageSize);
		 setPage(0);
		 Integer cnt = count(); 
		 if ( cnt== null)
			 cnt = 0 ; 
		 setTotalData(cnt);
		 holdedData = null ; 
		 navigate(0);
	}
	
	public void navigate (int page ) {
		setPage(page);
		adjustPagination();
		setHoldedData(  selectFromDB(getPageSize(), getFirstRowPosition()));
	}
	
	/**
	 * menghitung total data yang match dengan kriteria
	 */
	public abstract Integer count () ; 
	
	
	/**
	 * invoke query dari database. method ini menerima parameter ukuran page perpembacaan dan row pertama yang perlu di baca
	 */
	public abstract List<DATA> selectFromDB(int pageSize , int firstRowPosition);
	
	
	

	@Override
	public boolean addToSelection(DATA data) {
		if ( data == null || selectedItems.contains(data))
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
	public void setMultiple(boolean multiple) {
		this.multiple = multiple ; 
	}

	@Override
	public boolean isSelected(Object data) {
		return selectedItems.contains(data);
	}

	@Override
	public boolean isSelectionEmpty() {
		return selectedItems.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeFromSelection(Object data) {
		if (! selectedItems.contains(data))
			return false;
		selectedItems.add((DATA)data); 
		return true;
	}

	

	@Override
	public void setSelection(Collection<? extends DATA> selection) {
		selectedItems.clear(); 
		selectedItems.addAll(selection); 
		
	} 
	
	

}
