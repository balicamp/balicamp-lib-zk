package id.co.sigma.zk.ui.controller.master;

import java.io.Serializable;

import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.security.domain.lov.LookupHeader;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

/**
 * list untuk lookup header 
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public class LookupHeaderListController extends BaseSimpleListController<LookupHeader> implements IReloadablePanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6658612094472965933L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(LookupHeaderListController.class.getName());
	
	
	
	@Wire Listbox lsbLookupHeader ; 

	@QueryParameterEntry(filteredField="id", queryOperator=SimpleQueryFilterOperator.likeBothSide)
	@Wire 
	Textbox txtKode ; 
	
	@QueryParameterEntry(filteredField="remark", queryOperator=SimpleQueryFilterOperator.likeBothSide)
	@Wire 
	Textbox txtRemark; 
	
	@Wire Button btnSearch ;  
	@Wire Button btnReset ; 
	@Wire Button btnAdd; 
	
	@Override
	protected Class<? extends LookupHeader> getHandledClass() {
		return LookupHeader.class;
	}

	@Override
	public Listbox getListbox() {
		return lsbLookupHeader;
	}
	
	@Listen(value="onClick = #btnSearch")
	public void searchClick() {
		invokeSearch();
	};
	
	@Listen(value="onClick = #btnReset")
	public void resetClick() {
		txtKode.setValue("");
		txtRemark.setValue("");
//		invokeSearch();
	};
	
	@Listen(value="onClick = #btnAdd")
	public void addClick() {
		LookupHeader dataBaru = new LookupHeader(); 
		EditorManager.getInstance().addNewData("~./zul/pages/master/LookupHeaderEditor.zul", dataBaru, this);
	}

	@Override
	public void reload() {
		invokeSearch();
	}

	
}
