package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * Bandbox dgn fungsi autocomplete
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class BcBandbox extends Bandbox implements IdSpace, AfterCompose{
	
	private static final long serialVersionUID = -3806592381198871322L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BcBandbox.class.getName());
	
	private ListModelList<Object> listData;
	
	private BaseSimpleController controller;
	
	public ListModelList<Object> getListData() {
		return listData;
	}
	
	public void setListData(ListModelList<Object> listData) {
		this.listData = listData;
	}
	
	public BaseSimpleController getController() {
		return controller;
	}
	
	public void setController(BaseSimpleController controller) {
		this.controller = controller;
	}
	
	@Wire private Listbox listbox;
	
	public BcBandbox(){
		Executions.createComponents("~./zul/pages/common/BcBandbox.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);
	}
	
	public Object getObjectValue(){
		return listbox.getSelectedItem();
	}

	@Override
	public void afterCompose() {
		// Generate data list
		
		
		// Add event listener
		this.addEventListener("onChange", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				String val = getValue();
				if(val!=null && val.length()>=3){
					open();
					// TODO refresh list data
				}
			}
			
		});
	}
}
