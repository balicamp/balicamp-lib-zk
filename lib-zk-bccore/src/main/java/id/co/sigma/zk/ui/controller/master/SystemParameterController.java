package id.co.sigma.zk.ui.controller.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.security.domain.PageDefinition;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.common.data.app.SystemSimpleParameter;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

/**
 * 
 * @author <a href="mailto:rie.anggreani@gmail.com">Arie Anggreani</a>
 */
@Controller
public class SystemParameterController extends BaseSimpleListController<SystemSimpleParameter> implements IReloadablePanel {

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final Logger logger = LoggerFactory.getLogger(SystemSimpleParameter.class.getName());
	
		@QueryParameterEntry(
	                filteredField="id" , 
	                queryOperator=SimpleQueryFilterOperator.likeBothSide)
	    @Wire
	    Textbox keySearch ;
	    @QueryParameterEntry(filteredField="remark" , queryOperator= SimpleQueryFilterOperator.equal)
	    @Wire
	    Textbox remarkSearch  ;
	    @Wire
	    Button searchButton ;
	    @Wire
	    Listbox listbox ; 
	    @Wire Button resetButton ;
		@Wire Button addButton ; 

		@Wire Button btnEdit ; 
		@Wire Button btnHapus ;
 

	
	    @Autowired
		IGeneralPurposeService generalPurposeService;
	    
	    @Override
	    protected Class<? extends SystemSimpleParameter> getHandledClass() {
	        return SystemSimpleParameter.class;
	    }
	    @Override
	    public void doAfterCompose(Component comp) throws Exception {
	        super.doAfterCompose(comp);
	    }

	    @Listen(value="onClick = #searchButton")
	    public void click() {
	        invokeSearch();
	    };

	    @Override
	    public Listbox getListbox() {
	        return listbox;
	    }
	    
	    @Listen(value="onClick = #addButton")
	    public void clickAdd(){
	    	SystemSimpleParameter simpleParameter = new SystemSimpleParameter();
	    	EditorManager.getInstance().addNewData("~./zul/pages/master/SystemParameterFormEditor.zul", simpleParameter, this);
	    }
	    
	    /*@Listen("onEdit=#listbox")
		public void onEdit(ForwardEvent event){
			Button btn = (Button)event.getOrigin().getTarget();
			Listitem item = (Listitem)btn.getParent().getParent();
			SystemSimpleParameter simpleParameter = (SystemSimpleParameter)item.getValue();
			Executions.getCurrent().setAttribute("system", simpleParameter);
			EditorManager.getInstance().editData("~./zul/pages/master/SystemParameterFormEditor.zul", simpleParameter, this);
		}
	   
	    
	 	@Listen("onDelete=#listbox")
		public void onDelete(ForwardEvent event) {
			try {
				Button btn = (Button)event.getOrigin().getTarget();
				Listitem item = (Listitem)btn.getParent().getParent();
				SystemSimpleParameter system = (SystemSimpleParameter)item.getValue();
				
				SystemSimpleParameter deleteParameter =  generalPurposeDao.get(SystemSimpleParameter.class, system.getId()); 
				generalPurposeService.delete( deleteParameter);
				
				invokeSearch();
				Messagebox.show("Data berhasil dihapus");
			} catch (Exception e) {
				e.printStackTrace();
				Messagebox.show("Gagal menghapus data!");
			}
		}*/
	    
	    
		@Override
		public void reload() {
			invokeSearch();
		}
		
		@Listen(value="onClick = #resetButton")
		public void resetClick() {
			keySearch.setValue("");
			remarkSearch.setValue("");
			invokeSearch();
		};
		
		@Override
		public void deleteData(SystemSimpleParameter data) {
			deleteData(data, data.getId(), "id");
			reload();
		}

	    
	    
}
