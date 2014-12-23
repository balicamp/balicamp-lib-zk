package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.ui.SingleValueLookupReciever;
import id.co.sigma.zk.ui.controller.ZKEditorState;

import java.util.Map;
import java.util.Set;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * base class untuk lookup dengan hasil tunggal. 
 * ini memanfaatkan fungsi do-modal. jadinya anda perlu mengikuti hal berikut ini : 
 * <ol>
 * 	<li>harus mempergunakan tag window</li>
 * 	<li>Window sendiri harus di wire , dan di kembalikan dalam method :{@link BaseSingleResultLookupPanel#getWindowReference()}</li>
 * 
 * </ol>
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSingleResultLookupPanel<DATA> extends BaseHaveListboxController<DATA>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -353943955025121342L;
	
	
	
	private SingleValueLookupReciever<DATA> valueSelectedHandler ; 
	
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		Map<?,?> passedParameter  = Executions.getCurrent().getArg();
		ComponentInfo info =  super.doBeforeCompose(page, parent, compInfo);
		valueSelectedHandler = (SingleValueLookupReciever<DATA>)passedParameter.get(ZKCoreLibConstant.AFTER_SELECTION_HANDLER); 
		return info ; 
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * reference ke window. ini di samakan dengan id window yang di pakai
	 */
	protected abstract Window getWindowReference () ; 
	
	@Listen("onClick = #btnReset")
	public void resetClick(final Event evt) {
		this.resetFilter();
		
	}
	
	
	@Listen("onClick = #btnCari")
	public void searchClick(final Event evt) {
		searchData();
		
	}
	
	@Listen("onClick = #btnPilih")
	public void selectClick(final Event evt) {
	    try {
		Set<DATA> sels =  this.dataModel.getSelection();
		if ( sels!= null && !sels.isEmpty()){
			selectedItem = sels.iterator().next(); 
		}
		
		
		if ( selectedItem== null){
			Messagebox.show("Belum ada data yang di pilih");
			return ; 
		}
		validateData(selectedItem);
		getWindowReference().detach();
		valueSelectedHandler.onDataSelected(selectedItem);
	    } catch (Exception e) {
		Messagebox.show(
			e.getMessage(), 
			Labels.getLabel("title.msgbox.error"),
			new Messagebox.Button[]{Messagebox.Button.OK},
			new String[]{Labels.getLabel("action.button.ok")},
			Messagebox.ERROR,
			Messagebox.Button.OK, null);
	    }
		
	}
	
	
	@Listen("onClick = #btnCancel")
	public void cancelClick(final Event evt) {
		getWindowReference().detach();
	}
	
	protected void validateData(DATA dataToValidate) throws Exception{
	    
	}

}
