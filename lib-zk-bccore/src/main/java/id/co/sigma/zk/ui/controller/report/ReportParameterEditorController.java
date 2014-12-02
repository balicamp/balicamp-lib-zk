package id.co.sigma.zk.ui.controller.report;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.report.domain.RptDocParam;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleNoDirectToDBEditor;

public class ReportParameterEditorController extends
		BaseSimpleNoDirectToDBEditor<RptDocParam> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1367577843402068150L;
	
	@Wire	
	private Textbox paramCode;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if(ZKEditorState.ADD_NEW.equals(getEditorState())) {
			paramCode.setReadonly(false);
		}
	}
	
	

}
