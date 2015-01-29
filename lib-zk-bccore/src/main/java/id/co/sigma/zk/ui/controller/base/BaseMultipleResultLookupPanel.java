package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.zk.ZKCoreLibConstant;
import id.co.sigma.zk.ui.MultipleValueLookupReceiver;
import id.co.sigma.zk.ui.MultipleValueLookupReceiverWithValidation;

import java.util.ArrayList;
import java.util.List;
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
 * base class untuk lookup dengan hasil multiple. 
 * ini memanfaatkan fungsi do-modal. jadinya perlu mengikuti hal berikut ini : 
 * <ol>
 * 	<li>harus mempergunakan tag window</li>
 * 	<li>Window sendiri harus di wire , dan di kembalikan dalam method :{@link BaseMultipleResultLookupPanel#getWindowReference()}</li>
 * 
 * </ol>
 * 
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */
public abstract class BaseMultipleResultLookupPanel<DATA> extends
BaseHaveListboxController<DATA> {
    /**
     * 
     */
    private static final long serialVersionUID = 1619670011582131857L;
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
	    .getLogger(BaseMultipleResultLookupPanel.class.getName());


    protected MultipleValueLookupReceiver<DATA> valueSelectedHandler ; 


    @SuppressWarnings("unchecked")
    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent,
	    ComponentInfo compInfo) {
	Map<?,?> passedParameter  = Executions.getCurrent().getArg();
	ComponentInfo info =  super.doBeforeCompose(page, parent, compInfo);
	valueSelectedHandler = (MultipleValueLookupReceiver<DATA>)passedParameter.get(ZKCoreLibConstant.AFTER_SELECTION_HANDLER); 
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
	    if(this.dataModel!=null){
			Set<DATA> sels =  this.dataModel.getSelection();
			if ( sels!= null && !sels.isEmpty()){
			    multipleSelectedItem = new ArrayList<>(sels);	    		    
			}
	
	
			if ( multipleSelectedItem== null){
			    Messagebox.show(Labels.getLabel("msg.warnings.no_item_selected"), Labels.getLabel("title.msgbox.error"), Messagebox.OK, Messagebox.ERROR);
			    return ; 
			}
			validateData(multipleSelectedItem);
			getWindowReference().detach();
			valueSelectedHandler.onDataSelected(multipleSelectedItem);
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

    protected void validateData(List<DATA> dataToValidate) throws Exception{
	if ( this.valueSelectedHandler instanceof MultipleValueLookupReceiverWithValidation) {
	    ((MultipleValueLookupReceiverWithValidation<DATA>)valueSelectedHandler).validateSelectedData(dataToValidate); 
	}
    }
}
