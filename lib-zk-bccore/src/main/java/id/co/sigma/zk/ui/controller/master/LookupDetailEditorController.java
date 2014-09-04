package id.co.sigma.zk.ui.controller.master;


import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;

import id.co.sigma.common.security.domain.lov.LookupDetail;
import id.co.sigma.zk.ui.annotations.ControlDataBinder;
import id.co.sigma.zk.ui.controller.EditorManager;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleNoDirectToDBEditor;

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
	@Wire Textbox txtCode ;
	@Wire Textbox txtLabel;
	@Wire Textbox txtAdditionalData1 ;
	@Wire Textbox txtAdditionalData2;
	@Wire Intbox txtSequence ;
	@Wire Button btnSave;
	@Wire Button btnCancel;
	
	
	@Listen(value="onClick = #btnSave")
	public void saveClick() { 
		editedData.setDetailCode(txtCode.getValue());
		editedData.setExtField1(txtAdditionalData1.getValue());
		editedData.setExtField2(txtAdditionalData2.getValue());
		editedData.setI18Key(txtI18n.getValue());
		editedData.setLabel(txtLabel.getValue());
		
		
		
		try {
			if ( ZKEditorState.ADD_NEW.equals(  getEditorState())){
				insertData();
			}else if ( ZKEditorState.EDIT.equals(  getEditorState())){
				updateData();
			}
		} catch (Exception e) {
			logger.error("gagal save/insert data.error : " + e.getMessage() );
		}
		
		/**/
		
	}
	
	
	@Listen(value="onClick = #btnCancel")
	public void cancelClick() { 
		EditorManager.getInstance().closeCurrentEditorPanel();
	}
	
	
}
