package id.co.sigma.zk.ui.controller.master;


import id.co.sigma.common.security.domain.lov.LookupDetail;
import id.co.sigma.zk.ui.annotations.ControlDataBinder;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleNoDirectToDBEditor;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;

/**
 * editor Lookup detail
 * @author <a href="mailto:gede.sutarsa@gmail.com">Gede Sutarsa</a>
 */
public class LookupDetailEditorController extends BaseSimpleNoDirectToDBEditor<LookupDetail>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -784646505225622880L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(LookupDetailEditorController.class.getName());
	
	@ControlDataBinder(targetField="i18Key")
	@Wire Textbox txtI18n ;
	
	@ControlDataBinder(targetField="detailCode")
	@Wire Textbox txtCode ;
	
	@ControlDataBinder(targetField="label")
	@Wire Textbox txtLabel;
	
	@ControlDataBinder(targetField="extField1")
	@Wire Textbox txtAdditionalData1 ;
	
	@ControlDataBinder(targetField="extField2")
	@Wire Textbox txtAdditionalData2;
	
	@ControlDataBinder(targetField="sequence")
	@Wire Intbox txtSequence ;
	
	@Override	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if(ZKEditorState.ADD_NEW.equals(getEditorState())) {
			txtCode.setReadonly(false);
		}
	}

	
	
}
