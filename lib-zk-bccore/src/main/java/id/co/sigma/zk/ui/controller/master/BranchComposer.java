package id.co.sigma.zk.ui.controller.master;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka Darmawan</a>
 */
public class BranchComposer extends BaseSimpleListController<Branch> implements IReloadablePanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(BranchComposer.class.getName());

	
	@QueryParameterEntry(filteredField="branchCode", queryOperator=SimpleQueryFilterOperator.likeBothSide)
	@Wire
	private Textbox txtCariKode;
	
	@QueryParameterEntry(filteredField="branchName", queryOperator=SimpleQueryFilterOperator.likeBothSide)
	@Wire
	private Textbox txtCariNama;
	
	@Wire
	private Button btnCari;
	
	@Wire
	private Button btnReset;
	
	@Wire
	private Button addButton;
	
	@Wire
	private Listbox listbox;
	
	@Override
	protected Class<? extends Branch> getHandledClass() {
		return Branch.class;
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}


	@Listen(value="onClick = #btnCari")
    public void clickSearch() {
        invokeSearch();
    };
    
    @Listen(value="onClick = #btnReset")
    public void clickReset() {
        txtCariKode.setValue("");
        txtCariNama.setValue("");
    };
    
    @Listen(value="onClick = #addButton")
    public void onClickAdd() {
    	//Branch dataBranch = new Branch();
    	//EditorManager.getInstance().addNewData("/master/BranchEditor.zul", dataBranch, this);

	}

	@Override
	public Listbox getListbox() {
		return listbox;
	}

	@Override
	public void reload() {
		clickSearch();
	}
	
	
}