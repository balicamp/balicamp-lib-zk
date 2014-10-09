package id.co.sigma.zk.ui.controller.security;

import java.awt.event.MouseEvent;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
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
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

/**
 * controller untuk handler 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class PageDefintionListController extends BaseSimpleListController<PageDefinition> implements IReloadablePanel{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2670375216272230151L;
	@Wire
	Listbox pageListBox ;
	
	
	
	@QueryParameterEntry(filteredField="pageCode" , 
			queryOperator=SimpleQueryFilterOperator.likeBothSide ) 
	@Wire Textbox txtPageCode	;
	@QueryParameterEntry(filteredField="pageUrl" ,
			queryOperator=SimpleQueryFilterOperator.likeBothSide )
	@Wire Textbox txtPagUrl ;
	@QueryParameterEntry(filteredField="remark" , 
			queryOperator=SimpleQueryFilterOperator.likeBothSide )
	@Wire Textbox txtPageRemark; 
	
	
	@Override
	protected Class<? extends PageDefinition> getHandledClass() {
		return PageDefinition.class;
	}

	@Override
	public Listbox getListbox() {
		return pageListBox;
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		invokeSearch();
	}
	
	@Override
	public void reload() {
		invokeSearch();
		
	}

	@Override
	public void deleteData(PageDefinition data) {
		deleteData(data, data.getId(), "id");
		reload();
	}

	/* (non-Javadoc)
	 * @see id.co.sigma.zk.ui.controller.base.BaseSimpleListController#addNewData()
	 */
	@Override
	public PageDefinition addNewData() {
		return new PageDefinition();
	}

	

}
