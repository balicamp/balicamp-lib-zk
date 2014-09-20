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
	
	
	@Wire Button btnCari ;
	@Wire Button btnReset ; 
	@Wire Button btnTambah ; 
	
	@Wire Button btnEdit ; 
	@Wire Button btnHapus ; 

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
	@Listen(value="onClick = #btnCari")
	public void searchClick() {
		invokeSearch();
	};
	
	@Listen(value="onClick = #btnReset")
	public void resetClick() {
		txtPageCode.setValue("");
		txtPageRemark.setValue("");
		txtPagUrl.setValue("");
		invokeSearch();
		
	};
	
	@Listen(value="onClick = #btnTambah")
	public void addClick() {
		PageDefinition def = new PageDefinition(); 
		EditorManager.getInstance().addNewData("~./zul/pages/master/security/PageDefinitionEditor.zul", def, this);
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

	
	
//	@Listen("onEditData = #pageListBox")
//	public void clickEdit(ForwardEvent e) {
//		Component item = e.getOrigin().getTarget().getParent().getParent();
//		if(item instanceof Listitem) {
//			PageDefinition pDef = ((Listitem)item).getValue();
//			EditorManager.getInstance().editData("~./zul/pages/master/security/PageDefinitionEditor.zul", pDef, this);
//		}
//	}
//	
//	@Listen("onDeleteData = #pageListBox")
//	public void clickDelete(ForwardEvent e) {
//		Component item = e.getOrigin().getTarget().getParent().getParent();
//		if(item instanceof Listitem) {
//			PageDefinition pDef = ((Listitem)item).getValue();
//			deleteData(pDef, pDef.getId(), "id");
//			reload();
//		}
//	}
	
//	@Listen(value="onClick = #btnHapus")
//	public void hapusClick() {
//		
//	}
	
//	@Listen(value="onSelect = #pageListBox")
//	public void listboxSelected () {
//		System.out.println("selected");
//		btnHapus.setVisible(true); 
//		btnEdit.setVisible(true);
//	}
//	
//	@Listen(value="onClick =#pageListBox")
//	public void onEditOnGridClick(ForwardEvent evt ) {
//		Messagebox.show("Test click Grid"); 
//	}
	

}
