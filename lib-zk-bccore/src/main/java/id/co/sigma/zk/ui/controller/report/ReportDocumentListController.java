package id.co.sigma.zk.ui.controller.report;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.report.domain.RptDocParam;
import id.co.sigma.common.report.domain.RptDocument;
import id.co.sigma.zk.ui.annotations.QueryParameterEntry;
import id.co.sigma.zk.ui.controller.IReloadablePanel;
import id.co.sigma.zk.ui.controller.base.BaseSimpleListController;

public class ReportDocumentListController extends BaseSimpleListController<RptDocument> implements IReloadablePanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Wire
	private Listbox listRptDocument;
	
	@QueryParameterEntry(filteredField="id", queryOperator=SimpleQueryFilterOperator.equal, fieldType=Integer.class)
	@Wire
	private Intbox intCode;
	
	@Wire
	@QueryParameterEntry(filteredField="name", queryOperator=SimpleQueryFilterOperator.likeBothSide)
	private Textbox txtName;
	
	@Wire
	@QueryParameterEntry(filteredField="description", queryOperator=SimpleQueryFilterOperator.likeBothSide)
	private Textbox txtDescription;
	
	@Value(value="${common.format.ui.date}")
	private String dateFormat;

	@Override
	public void reload() {
		invokeSearch();
	}

	@Override
	protected Class<? extends RptDocument> getHandledClass() {
		return RptDocument.class;
	}

	@Override
	public Listbox getListbox() {
		return listRptDocument;
	}

	/* (non-Javadoc)
	 * @see id.co.sigma.zk.ui.controller.base.BaseSimpleListController#getChildrenParentKeyAndEntiy()
	 */
	@Override
	protected Map<String, Class<?>> getChildrenParentKeyAndEntiy() {
		Map<String, Class<?>> child = new HashMap<String, Class<?>>();
		child.put("rptDocId", RptDocParam.class);
		return child;
	}

}
