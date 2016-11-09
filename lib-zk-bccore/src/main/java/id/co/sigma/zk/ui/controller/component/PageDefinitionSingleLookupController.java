package id.co.sigma.zk.ui.controller.component;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.security.domain.PageDefinition;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.base.BaseSingleResultLookupPanel;

/**
 * controller budget usage detail list
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka D.</a>
 */
public class PageDefinitionSingleLookupController extends BaseSingleResultLookupPanel<PageDefinition> {

	@Wire
	private Listbox listbox;

	@Wire
	private Window pageDefinitionLookupWin;

	@QueryParameterEntry(filteredField = "pageCode", queryOperator = SimpleQueryFilterOperator.likeBothSide, fieldType = String.class)
	@Wire private Textbox txtCode;
	
	@QueryParameterEntry(filteredField = "pageUrl", queryOperator = SimpleQueryFilterOperator.likeBothSide, fieldType = String.class)
	@Wire private Textbox txtUrl;
	
	@Override
	protected Window getWindowReference() {
		return pageDefinitionLookupWin;
	}

	@Override
	protected Class<? extends PageDefinition> getHandledClass() {
		return PageDefinition.class;
	}

	@Override
	public Listbox getListbox() {
		return listbox;
	}

}
