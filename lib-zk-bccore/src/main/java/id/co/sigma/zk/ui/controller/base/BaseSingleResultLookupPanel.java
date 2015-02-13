package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.ui.SingleValueLookupReciever;
import id.co.sigma.zk.ui.SingleValueLookupRecieverWithValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

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
	
	
	
	protected SingleValueLookupReciever<DATA> valueSelectedHandler ;
	
	@Wire
	Button btnCari;
	
	@SuppressWarnings("unchecked")
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		Map<?,?> passedParameter  = Executions.getCurrent().getArg();
		ComponentInfo info =  super.doBeforeCompose(page, parent, compInfo);
		valueSelectedHandler = (SingleValueLookupReciever<DATA>)passedParameter.get(ZKCoreLibConstant.AFTER_SELECTION_HANDLER); 
		return info ; 
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		setEnterKeyListener();
		setEscKeyListener();
	}
	
	/**
	 * Set 'Enter' key listener on input elements (except for Button)
	 */
	private void setEnterKeyListener(){
		EventListener<Event> okEvent = new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if(!(event.getTarget() instanceof Button)) {
					Events.sendEvent("onClick", btnCari, null);
				}
			}
		};
		
		List<Component> comps = new ArrayList<Component>(getWindowReference().getFellows());
		if(comps.size()>0){
			for(Component comp : comps){
				if(comp instanceof InputElement){
					comp.addEventListener("onOK", okEvent);
				}
			}
		}
	}
	
	
	/**
	 * Set 'ESC' key listener
	 */
	private void setEscKeyListener(){
	    
	    getWindowReference().addEventListener("onCancel", new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
		    cancelClick(event);
		}
	    });
	}
		
		
	/**
	 * reference ke window. ini di samakan dengan id window yang di pakai
	 */
	protected abstract Window getWindowReference () ; 
	
	@Listen("onClick = #btnReset")
	public void resetClick(final Event evt) {
		this.resetFilter();
		selectedItem = null;
	}
	
	
	@Listen("onClick = #btnCari")
	public void searchClick(final Event evt) {
		searchData();
		selectedItem = null;
	}
	
	@Listen("onClick = #btnPilih")
	public void selectClick(final Event evt) {
	    try {
	    	if(this.dataModel!=null){
	    		Set<DATA> sels =  this.dataModel.getSelection();
	    		if ( sels!= null && !sels.isEmpty()){
	    			selectedItem = sels.iterator().next(); 
	    		}else{
	    		    selectedItem=null;
	    		}
	    		
	    		
	    		if ( selectedItem== null){
	    			Messagebox.show(Labels.getLabel("msg.warnings.no_item_selected"), Labels.getLabel("title.msgbox.error"), Messagebox.OK, Messagebox.ERROR);
	    			return ; 
	    		}
	    		validateData(selectedItem);
	    		getWindowReference().detach();
	    		valueSelectedHandler.onDataSelected(selectedItem);
	    	}else{
	    		Messagebox.show(Labels.getLabel("msg.warnings.no_item_selected"), Labels.getLabel("title.msgbox.error"), Messagebox.OK, Messagebox.ERROR);
	    	}
		
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
	    if ( this.valueSelectedHandler instanceof SingleValueLookupRecieverWithValidation ) {
	    	((SingleValueLookupRecieverWithValidation<DATA>)valueSelectedHandler).validateSelectedData(dataToValidate); 
	    }
	}

}
