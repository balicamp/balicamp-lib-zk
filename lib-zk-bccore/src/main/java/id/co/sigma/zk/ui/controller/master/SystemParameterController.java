package id.co.sigma.zk.ui.controller.master;

import id.co.sigma.common.data.app.SystemSimpleParameter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.server.service.IGeneralPurposeService;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

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

	    @Override
	    public Listbox getListbox() {
	        return listbox;
	    }
	    
		@Override
		public void reload() {
			invokeSearch();
		}
		
		@Override
		public void deleteData(SystemSimpleParameter data) {
			deleteData(data, data.getId(), "id");
			reload();
		}
		/* (non-Javadoc)
		 * @see id.co.sigma.zk.ui.controller.base.BaseSimpleListController#addNewData()
		 */
		@Override
		public SystemSimpleParameter addNewData() {
			return new SystemSimpleParameter();
		}

	    
	    
}
